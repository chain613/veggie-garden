package com.veggie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "t_npc_dialog")
public class NpcDialog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "trigger_event", nullable = false, length = 100)
    private String triggerEvent;

    @Column(name = "vegetable_id")
    private Integer vegetableId;

    @Column(length = 10)
    private String season;

    @Column(name = "dialog_text", nullable = false, columnDefinition = "TEXT")
    private String dialogText;

    public Integer getId() { return id; }
    public String getTriggerEvent() { return triggerEvent; }
    public Integer getVegetableId() { return vegetableId; }
    public String getSeason() { return season; }
    public String getDialogText() { return dialogText; }
}
