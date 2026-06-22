package com.veggie.service;

import com.veggie.model.*;
import com.veggie.repository.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GrowthTickService {
    private final PlantRepository plantRepo;
    private final PlantFruitRepository fruitRepo;
    private final VegetableRepository vegRepo;
    private final WeedRepository weedRepo;
    private final WeatherService weatherService;

    public GrowthTickService(PlantRepository plantRepo, PlantFruitRepository fruitRepo,
                             VegetableRepository vegRepo, WeedRepository weedRepo,
                             WeatherService weatherService) {
        this.plantRepo = plantRepo;
        this.fruitRepo = fruitRepo;
        this.vegRepo = vegRepo;
        this.weedRepo = weedRepo;
        this.weatherService = weatherService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void dailyTick() {
        weatherService.generateWeather();
        List<Plant> allPlants = plantRepo.findAll();
        for (Plant plant : allPlants) {
            tickPlant(plant);
        }
        List<Long> gardenIds = plantRepo.findAll().stream()
            .map(Plant::getGardenId).distinct().toList();
        for (Long gardenId : gardenIds) {
            tickWeeds(gardenId);
        }
    }

    private void tickWeeds(Long gardenId) {
        List<Weed> weeds = weedRepo.findByGardenId(gardenId);
        List<Plant> plants = plantRepo.findByGardenId(gardenId);
        int month = LocalDate.now().getMonthValue();
        boolean rainySeason = (month >= 3 && month <= 8);

        for (Weed w : weeds) {
            long daysSinceAppeared = ChronoUnit.DAYS.between(w.getAppearedAt(), LocalDate.now());
            if (daysSinceAppeared >= 5) w.setGrowthStage(2);
            else if (daysSinceAppeared >= 2) w.setGrowthStage(1);
            weedRepo.save(w);

            if (w.getGrowthStage() >= 1) {
                int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
                for (int[] d : dirs) {
                    int nx = w.getGridX() + d[0];
                    int ny = w.getGridY() + d[1];
                    if (nx < 0 || nx >= 15 || ny < 0 || ny >= 15) continue;
                    if (isGridEmpty(gardenId, nx, ny, plants, weeds)) {
                        if (Math.random() < 0.20) {
                            Weed newWeed = new Weed();
                            newWeed.setGardenId(gardenId);
                            newWeed.setGridX(nx);
                            newWeed.setGridY(ny);
                            newWeed.setAppearedAt(LocalDate.now());
                            weedRepo.save(newWeed);
                        }
                    }
                }
            }
        }

        double germRate = rainySeason ? 0.12 : 0.05;
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                if (isGridEmpty(gardenId, x, y, plants, weeds)) {
                    if (Math.random() < germRate) {
                        Weed w = new Weed();
                        w.setGardenId(gardenId);
                        w.setGridX(x);
                        w.setGridY(y);
                        w.setAppearedAt(LocalDate.now());
                        weedRepo.save(w);
                    }
                }
            }
        }
    }

    private boolean isGridEmpty(Long gardenId, int x, int y,
                                 List<Plant> plants, List<Weed> weeds) {
        boolean hasPlant = plants.stream()
            .anyMatch(p -> p.getGridX() == x && p.getGridY() == y);
        boolean hasWeed = weeds.stream()
            .anyMatch(w -> w.getGridX() == x && w.getGridY() == y);
        return !hasPlant && !hasWeed;
    }

    private void tickPlant(Plant plant) {
        Vegetable veg = vegRepo.findById(plant.getVegetableId()).orElse(null);
        if (veg == null) return;

        if (plant.getRootMass() == null) {
            plant.setRootMass(new BigDecimal("0.05"));
            plant.setLeafMass(new BigDecimal("0.05"));
            plant.setStemMass(new BigDecimal("0.02"));
            plant.setGlucose(BigDecimal.ZERO);
            plant.setMicroIndex(new BigDecimal("0.80"));
            plant.setVentilation(new BigDecimal("1.00"));
            checkSeasonMismatch(plant, veg);
        }

        if (plant.getBolting() != null && plant.getBolting()) {
            tickBolting(plant, veg);
            plantRepo.save(plant);
            return;
        }

        BigDecimal glucose = photosynthesis(plant, veg);
        BigDecimal waterAbsorbed = rootWaterAbsorb(plant);
        BigDecimal nutrientAbsorbed = rootNutrientAbsorb(plant);
        updateVentilation(plant);
        allocateGlucose(plant, veg, glucose);
        updateWaterFromAbsorption(plant, waterAbsorbed);
        updateNutrientFromAbsorption(plant, nutrientAbsorbed);

        BigDecimal health = calculateHealth(plant, glucose, waterAbsorbed, nutrientAbsorbed);
        plant.setHealthScore(health);

        applyCompetitionWeakening(plant, veg);

        long daysSincePlanted = ChronoUnit.DAYS.between(plant.getPlantedAt(), LocalDate.now());
        double progress = (double) daysSincePlanted / veg.getGrowthDays();
        int stage = progress < 0.1 ? 0 : progress < 0.4 ? 1 : progress < 0.8 ? 2 : 3;
        plant.setGrowthStage(stage);

        if (veg.getHarvestType() == Vegetable.HarvestType.continuous && stage >= 3) {
            plant.setIsFruiting(true);
            tickFruiting(plant, veg);
        }

        plantRepo.save(plant);
    }

    private void checkSeasonMismatch(Plant plant, Vegetable veg) {
        int month = LocalDate.now().getMonthValue();
        String currentSeason;
        if (month >= 3 && month <= 5) currentSeason = "春";
        else if (month >= 6 && month <= 8) currentSeason = "夏";
        else if (month >= 9 && month <= 11) currentSeason = "秋";
        else currentSeason = "冬";

        if (!veg.getSeason().contains(currentSeason)) {
            plant.setBolting(true);
            plant.setGrowthStage(0);
        }
    }

    private void tickBolting(Plant plant, Vegetable veg) {
        long daysSincePlanted = ChronoUnit.DAYS.between(plant.getPlantedAt(), LocalDate.now());
        if (daysSincePlanted >= 10) plant.setGrowthStage(3);
        else if (daysSincePlanted >= 4) plant.setGrowthStage(2);
        else if (daysSincePlanted >= 1) plant.setGrowthStage(1);

        plant.setLeafMass(plant.getLeafMass().add(new BigDecimal("0.002")));
        plant.setStemMass(plant.getStemMass().add(new BigDecimal("0.015")));
        plant.setRootMass(plant.getRootMass().add(new BigDecimal("0.001")));

        BigDecimal health = plant.getHealthScore();
        if (health.compareTo(new BigDecimal("0.40")) > 0) {
            plant.setHealthScore(new BigDecimal("0.40"));
        }

        if (veg.getHarvestType() == Vegetable.HarvestType.continuous
            && plant.getGrowthStage() >= 3) {
            plant.setIsFruiting(true);
            List<PlantFruit> existing = fruitRepo.findByPlantId(plant.getId());
            if (existing.size() < 2 && daysSincePlanted % 10 == 0) {
                PlantFruit fruit = new PlantFruit();
                fruit.setPlantId(plant.getId());
                fruit.setAppearedAt(LocalDate.now());
                fruit.setMatureAt(LocalDate.now().plusDays(veg.getFruitMatureDays() != null
                    ? veg.getFruitMatureDays() : 5));
                fruit.setOverripeAt(LocalDate.now().plusDays(
                    (veg.getFruitMatureDays() != null ? veg.getFruitMatureDays() : 5)
                    + (veg.getFruitOverripeDays() != null ? veg.getFruitOverripeDays() : 3)));
                fruit.setQualityScore(new BigDecimal("0.25"));
                fruitRepo.save(fruit);
            }
        }
    }

    private void applyCompetitionWeakening(Plant plant, Vegetable veg) {
        BigDecimal lightIntensity = getLightIntensity(plant);
        BigDecimal nutrientDiscount = getNutrientCompetitionDiscount(plant);

        if (lightIntensity.compareTo(new BigDecimal("0.40")) < 0) {
            plant.setLeafMass(plant.getLeafMass().multiply(new BigDecimal("0.50"))
                .add(plant.getLeafMass().multiply(new BigDecimal("0.005"))));
            plant.setStemMass(plant.getStemMass().add(new BigDecimal("0.008")));
            plant.setHealthScore(plant.getHealthScore().subtract(new BigDecimal("0.05")));
            if (plant.getHealthScore().compareTo(BigDecimal.ZERO) < 0)
                plant.setHealthScore(BigDecimal.ZERO);
        }

        if (nutrientDiscount.compareTo(new BigDecimal("0.60")) < 0) {
            plant.setRootMass(plant.getRootMass().multiply(new BigDecimal("0.50"))
                .add(plant.getRootMass().multiply(new BigDecimal("0.003"))));
            plant.setStemMass(plant.getStemMass().multiply(new BigDecimal("0.95")));
            plant.setHealthScore(plant.getHealthScore().subtract(new BigDecimal("0.03")));
            if (plant.getHealthScore().compareTo(BigDecimal.ZERO) < 0)
                plant.setHealthScore(BigDecimal.ZERO);
        }
    }

    private BigDecimal getNutrientCompetitionDiscount(Plant plant) {
        BigDecimal discount = BigDecimal.ONE;
        List<Plant> neighbors = plantRepo.findByGardenId(plant.getGardenId());
        for (Plant other : neighbors) {
            if (other.getId().equals(plant.getId())) continue;
            int dx = Math.abs(plant.getGridX() - other.getGridX());
            int dy = Math.abs(plant.getGridY() - other.getGridY());
            if (dx <= 1 && dy <= 1) {
                discount = discount.multiply(new BigDecimal("0.75"));
            }
        }
        return discount;
    }

    private BigDecimal getWeedCompetitionDiscount(Plant plant) {
        BigDecimal discount = BigDecimal.ONE;
        List<Weed> weeds = weedRepo.findByGardenId(plant.getGardenId());
        int adjacentWeeds = 0;
        for (Weed w : weeds) {
            int dx = Math.abs(plant.getGridX() - w.getGridX());
            int dy = Math.abs(plant.getGridY() - w.getGridY());
            if (dx <= 1 && dy <= 1) adjacentWeeds++;
        }
        for (int i = 0; i < adjacentWeeds; i++) {
            discount = discount.multiply(new BigDecimal("0.85"));
        }
        if (discount.compareTo(new BigDecimal("0.30")) < 0)
            discount = new BigDecimal("0.30");
        return discount;
    }

    private BigDecimal photosynthesis(Plant plant, Vegetable veg) {
        BigDecimal lightIntensity = getLightIntensity(plant);
        BigDecimal leafMass = plant.getLeafMass();
        BigDecimal co2 = plant.getVentilation();
        BigDecimal waterCoef = plant.getWaterLevel();
        BigDecimal tempCoef = getTemperatureCoefficient();

        BigDecimal glucose = lightIntensity
            .multiply(leafMass)
            .multiply(co2)
            .multiply(waterCoef)
            .multiply(tempCoef)
            .multiply(new BigDecimal("10.0"));

        plant.setGlucose(glucose);
        return glucose;
    }

    private BigDecimal getLightIntensity(Plant plant) {
        String weather = weatherService.getCurrentWeather();
        BigDecimal base = switch (weather) {
            case "晴天" -> new BigDecimal("1.00");
            case "阴天" -> new BigDecimal("0.50");
            case "下雨" -> new BigDecimal("0.35");
            case "大风" -> new BigDecimal("0.70");
            default -> new BigDecimal("0.80");
        };

        List<Plant> neighbors = plantRepo.findByGardenId(plant.getGardenId());
        Vegetable thisVeg = vegRepo.findById(plant.getVegetableId()).orElse(null);
        for (Plant other : neighbors) {
            if (other.getId().equals(plant.getId())) continue;
            Vegetable otherVeg = vegRepo.findById(other.getVegetableId()).orElse(null);
            if (otherVeg == null || thisVeg == null) continue;
            if (otherVeg.getHeightLevel() > thisVeg.getHeightLevel()
                && other.getGridY() > plant.getGridY()
                && Math.abs(other.getGridX() - plant.getGridX()) <= 2) {
                base = base.subtract(new BigDecimal("0.15"));
            }
        }
        if (base.compareTo(new BigDecimal("0.1")) < 0) base = new BigDecimal("0.1");
        return base;
    }

    private BigDecimal rootWaterAbsorb(Plant plant) {
        BigDecimal rootMass = plant.getRootMass();
        BigDecimal soilMoisture = plant.getWaterLevel();
        BigDecimal tempCoef = getTemperatureCoefficient();
        BigDecimal weedDiscount = getWeedCompetitionDiscount(plant);

        return rootMass.multiply(soilMoisture)
            .multiply(tempCoef)
            .multiply(weedDiscount)
            .multiply(new BigDecimal("5.0"));
    }

    private BigDecimal rootNutrientAbsorb(Plant plant) {
        BigDecimal rootMass = plant.getRootMass();
        BigDecimal soilFertility = plant.getFertilizerLevel();
        BigDecimal microIndex = plant.getMicroIndex();
        BigDecimal plantDiscount = getNutrientCompetitionDiscount(plant);
        BigDecimal weedDiscount = getWeedCompetitionDiscount(plant);
        return rootMass.multiply(soilFertility)
            .multiply(microIndex)
            .multiply(plantDiscount)
            .multiply(weedDiscount)
            .multiply(new BigDecimal("3.0"));
    }

    private void updateVentilation(Plant plant) {
        BigDecimal base = BigDecimal.ONE;
        List<Plant> neighbors = plantRepo.findByGardenId(plant.getGardenId());
        int closeCount = 0;
        for (Plant other : neighbors) {
            if (other.getId().equals(plant.getId())) continue;
            int dx = Math.abs(plant.getGridX() - other.getGridX());
            int dy = Math.abs(plant.getGridY() - other.getGridY());
            if (dx <= 1 && dy <= 1) closeCount++;
        }
        if (closeCount >= 4) base = base.multiply(new BigDecimal("0.6"));
        if ("大风".equals(weatherService.getCurrentWeather())) {
            base = base.multiply(new BigDecimal("1.3"));
        }
        plant.setVentilation(base);
    }

    private void allocateGlucose(Plant plant, Vegetable veg, BigDecimal glucose) {
        int stage = plant.getGrowthStage();
        BigDecimal rootAlloc, leafAlloc, stemAlloc, fruitAlloc;

        switch (stage) {
            case 0:
                rootAlloc = new BigDecimal("0.40");
                leafAlloc = new BigDecimal("0.40");
                stemAlloc = new BigDecimal("0.20");
                fruitAlloc = BigDecimal.ZERO;
                break;
            case 1:
                rootAlloc = new BigDecimal("0.25");
                leafAlloc = new BigDecimal("0.35");
                stemAlloc = new BigDecimal("0.30");
                fruitAlloc = new BigDecimal("0.10");
                break;
            default:
                rootAlloc = new BigDecimal("0.15");
                leafAlloc = new BigDecimal("0.20");
                stemAlloc = new BigDecimal("0.25");
                fruitAlloc = new BigDecimal("0.40");
                break;
        }

        BigDecimal factor = new BigDecimal("0.01");
        plant.setRootMass(plant.getRootMass().add(glucose.multiply(rootAlloc).multiply(factor)));
        plant.setLeafMass(plant.getLeafMass().add(glucose.multiply(leafAlloc).multiply(factor)));
        plant.setStemMass(plant.getStemMass().add(glucose.multiply(stemAlloc).multiply(factor)));
    }

    private void updateWaterFromAbsorption(Plant plant, BigDecimal absorbed) {
        String weather = weatherService.getCurrentWeather();
        double consumption = 0.10;
        if ("晴天".equals(weather)) consumption = 0.15;
        if ("大风".equals(weather)) consumption = 0.18;
        if ("下雨".equals(weather)) consumption = -0.30;

        BigDecimal newLevel = plant.getWaterLevel()
            .subtract(new BigDecimal(consumption))
            .add(absorbed.multiply(new BigDecimal("0.5")));
        if (newLevel.compareTo(BigDecimal.ZERO) < 0) newLevel = BigDecimal.ZERO;
        if (newLevel.compareTo(BigDecimal.ONE) > 0) newLevel = BigDecimal.ONE;
        plant.setWaterLevel(newLevel);
    }

    private void updateNutrientFromAbsorption(Plant plant, BigDecimal absorbed) {
        BigDecimal newLevel = plant.getFertilizerLevel()
            .subtract(new BigDecimal("0.05"))
            .add(absorbed.multiply(new BigDecimal("0.3")));
        if (newLevel.compareTo(BigDecimal.ZERO) < 0) newLevel = BigDecimal.ZERO;
        if (newLevel.compareTo(BigDecimal.ONE) > 0) newLevel = BigDecimal.ONE;
        plant.setFertilizerLevel(newLevel);
    }

    private BigDecimal getTemperatureCoefficient() {
        int month = LocalDate.now().getMonthValue();
        if ((month >= 3 && month <= 5) || (month >= 9 && month <= 11)) {
            return new BigDecimal("1.00");
        } else if (month >= 6 && month <= 8) {
            return new BigDecimal("0.85");
        } else {
            return new BigDecimal("0.60");
        }
    }

    private BigDecimal calculateHealth(Plant plant, BigDecimal glucose,
                                        BigDecimal water, BigDecimal nutrient) {
        return glucose.multiply(new BigDecimal("0.25"))
            .add(water.multiply(new BigDecimal("0.20")))
            .add(nutrient.multiply(new BigDecimal("0.20")))
            .add(getTemperatureCoefficient().multiply(new BigDecimal("0.10")))
            .add(new BigDecimal("0.15"))
            .add(new BigDecimal("0.10"));
    }

    private void tickFruiting(Plant plant, Vegetable veg) {
        long daysSincePlanted = ChronoUnit.DAYS.between(plant.getPlantedAt(), LocalDate.now());
        long daysInFruiting = daysSincePlanted - (long)(veg.getGrowthDays() * 0.8);
        if (daysInFruiting > 0 && daysInFruiting % veg.getFruitIntervalDays() == 0) {
            PlantFruit fruit = new PlantFruit();
            fruit.setPlantId(plant.getId());
            fruit.setAppearedAt(LocalDate.now());
            fruit.setMatureAt(LocalDate.now().plusDays(veg.getFruitMatureDays()));
            fruit.setOverripeAt(LocalDate.now()
                .plusDays(veg.getFruitMatureDays() + veg.getFruitOverripeDays()));
            fruit.setQualityScore(BigDecimal.ONE);
            fruitRepo.save(fruit);
        }

        List<PlantFruit> fruits = fruitRepo.findByPlantIdAndHarvestedAtIsNull(plant.getId());
        int overripe = 0;
        for (PlantFruit f : fruits) {
            if (LocalDate.now().isAfter(f.getOverripeAt())) {
                f.setQualityScore(BigDecimal.ZERO);
                fruitRepo.save(f);
                overripe++;
            } else if (LocalDate.now().isAfter(f.getMatureAt())) {
                long daysSinceMature = ChronoUnit.DAYS.between(f.getMatureAt(), LocalDate.now());
                long maxDays = ChronoUnit.DAYS.between(f.getMatureAt(), f.getOverripeAt());
                if (maxDays > 0) {
                    double q = 1.0 - (double) daysSinceMature / maxDays;
                    f.setQualityScore(new BigDecimal(Math.max(0, q)));
                    fruitRepo.save(f);
                }
            }
        }

        if (overripe > 0) {
            plant.setHealthScore(plant.getHealthScore()
                .subtract(new BigDecimal(overripe).multiply(new BigDecimal("0.05"))));
        }
    }
}
