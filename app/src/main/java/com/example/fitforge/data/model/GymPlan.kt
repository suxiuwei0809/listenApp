package com.example.fitforge.data.model

data class GymPlan(
    val id: String,
    val name: String,
    val target: TrainingTarget,
    val steps: List<GymStep>
)

enum class TrainingTarget(val displayName: String) {
    BACK("练背"),
    CHEST("练胸"),
    LEGS("练腿"),
    ABS("腹肌"),
    FAT_LOSS("减脂")
}

data class GymStep(
    val name: String,
    val detail: String,
    val phase: TrainingPhase,
    val tip: String? = null,
    val sets: String? = null,
    val duration: String? = null
)

enum class TrainingPhase(val displayName: String) {
    WARMUP("热身"),
    MAIN("主训练"),
    STRETCH("拉伸")
}
