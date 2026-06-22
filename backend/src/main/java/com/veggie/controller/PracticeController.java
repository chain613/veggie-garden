package com.veggie.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class PracticeController {

    private static final List<Map<String, Object>> PRACTICES = List.of(
        Map.of("id", 1, "name", "新手种植", "description", "学习如何种植第一棵蔬菜",
            "steps", List.of(
                Map.of("order", 1, "action", "select_seed", "description", "从背包中选择油菜种子"),
                Map.of("order", 2, "action", "plant", "description", "点击空地种植种子"),
                Map.of("order", 3, "action", "water", "description", "用水壶给种子浇水")
            )),
        Map.of("id", 2, "name", "浇水与施肥", "description", "掌握浇水和施肥的技巧",
            "steps", List.of(
                Map.of("order", 1, "action", "water", "description", "给缺水的植株浇水"),
                Map.of("order", 2, "action", "fertilize", "description", "使用有机肥给植株施肥"),
                Map.of("order", 3, "action", "observe", "description", "观察植株健康度变化")
            )),
        Map.of("id", 3, "name", "除草行动", "description", "清除菜园里的杂草",
            "steps", List.of(
                Map.of("order", 1, "action", "check_weeds", "description", "检查菜园中是否有杂草"),
                Map.of("order", 2, "action", "remove_weed", "description", "使用小锄头清除杂草"),
                Map.of("order", 3, "action", "clean", "description", "清理所有杂草保持菜园整洁")
            )),
        Map.of("id", 4, "name", "丰收时刻", "description", "采收成熟的蔬菜",
            "steps", List.of(
                Map.of("order", 1, "action", "check_mature", "description", "找到已成熟的植株"),
                Map.of("order", 2, "action", "harvest", "description", "采收成熟的蔬菜"),
                Map.of("order", 3, "action", "warehouse", "description", "将收获存入仓库")
            ))
    );

    @GetMapping("/api/practice")
    public List<Map<String, Object>> list() {
        return PRACTICES.stream()
            .map(p -> Map.of("id", p.get("id"), "name", p.get("name"), "description", p.get("description")))
            .toList();
    }

    @GetMapping("/api/practice/{id}")
    public Map<String, Object> detail(@PathVariable int id) {
        return PRACTICES.stream()
            .filter(p -> ((Integer) p.get("id")) == id)
            .findFirst()
            .orElse(Map.of("error", "副本不存在"));
    }
}
