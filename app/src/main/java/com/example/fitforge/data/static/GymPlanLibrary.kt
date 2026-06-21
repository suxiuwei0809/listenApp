package com.example.fitforge.data.static

import com.example.fitforge.data.model.GymPlan
import com.example.fitforge.data.model.GymStep
import com.example.fitforge.data.model.TrainingPhase
import com.example.fitforge.data.model.TrainingTarget
import java.util.Calendar

/**
 * 健身房训练计划静态数据 —— 不需要 Room 数据库。
 */
object GymPlanLibrary {

    fun getRecommendTargetForToday(): TrainingTarget {
        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> TrainingTarget.BACK
            Calendar.TUESDAY -> TrainingTarget.CHEST
            Calendar.WEDNESDAY -> TrainingTarget.LEGS
            Calendar.THURSDAY -> TrainingTarget.ABS
            Calendar.FRIDAY -> TrainingTarget.FAT_LOSS
            else -> TrainingTarget.BACK
        }
    }

    fun getGymPlans(): List<GymPlan> = listOf(
        GymPlan(
            id = "back", name = "练背日", target = TrainingTarget.BACK,
            steps = getStepsForTarget(TrainingTarget.BACK)
        ),
        GymPlan(
            id = "chest", name = "练胸日", target = TrainingTarget.CHEST,
            steps = getStepsForTarget(TrainingTarget.CHEST)
        ),
        GymPlan(
            id = "legs", name = "练腿日", target = TrainingTarget.LEGS,
            steps = getStepsForTarget(TrainingTarget.LEGS)
        ),
        GymPlan(
            id = "abs", name = "练腹日", target = TrainingTarget.ABS,
            steps = getStepsForTarget(TrainingTarget.ABS)
        ),
        GymPlan(
            id = "fat", name = "减脂日", target = TrainingTarget.FAT_LOSS,
            steps = getStepsForTarget(TrainingTarget.FAT_LOSS)
        )
    )

    fun getStepsForTarget(target: TrainingTarget): List<GymStep> = when (target) {
        TrainingTarget.BACK -> listOf(
            GymStep("动态拉伸", "5分钟", TrainingPhase.WARMUP),
            GymStep("弹力带外旋", "2组 × 15次", TrainingPhase.WARMUP),
            GymStep("引体向上", "4组 × 8次", TrainingPhase.MAIN, "核心收紧，肩胛骨下沉发力"),
            GymStep("杠铃划船", "4组 × 10次", TrainingPhase.MAIN, "背部发力，不要用手臂拉"),
            GymStep("坐姿下拉", "3组 × 12次", TrainingPhase.MAIN, "挺胸，下拉到锁骨位置"),
            GymStep("单臂哑铃划船", "3组 × 10次/侧", TrainingPhase.MAIN, "肘部贴身"),
            GymStep("背部拉伸", "每组30秒", TrainingPhase.STRETCH),
            GymStep("猫牛式放松", "10次", TrainingPhase.STRETCH),
        )
        TrainingTarget.CHEST -> listOf(
            GymStep("肩部环绕", "2分钟", TrainingPhase.WARMUP),
            GymStep("俯卧撑热身", "2组 × 10次", TrainingPhase.WARMUP),
            GymStep("平板杠铃卧推", "4组 × 10次", TrainingPhase.MAIN, "肩胛骨收紧，下放到胸口"),
            GymStep("上斜哑铃卧推", "4组 × 10次", TrainingPhase.MAIN, "角度30-45度"),
            GymStep("蝴蝶机夹胸", "3组 × 12次", TrainingPhase.MAIN, "顶峰收缩停顿1秒"),
            GymStep("双杠臂屈伸", "3组 × 8次", TrainingPhase.MAIN, "身体前倾，重点在胸肌"),
            GymStep("胸肌门框拉伸", "每组30秒", TrainingPhase.STRETCH),
            GymStep("扩胸放松", "15次", TrainingPhase.STRETCH),
        )
        TrainingTarget.LEGS -> listOf(
            GymStep("腿部动态拉伸", "5分钟", TrainingPhase.WARMUP),
            GymStep("徒手深蹲", "2组 × 15次", TrainingPhase.WARMUP),
            GymStep("杠铃深蹲", "5组 × 8次", TrainingPhase.MAIN, "膝盖对准脚尖，蹲至大腿平行"),
            GymStep("腿举", "4组 × 12次", TrainingPhase.MAIN, "不要锁死膝盖"),
            GymStep("罗马尼亚硬拉", "4组 × 10次", TrainingPhase.MAIN, "感受腘绳肌拉伸"),
            GymStep("保加利亚分腿蹲", "3组 × 10次/侧", TrainingPhase.MAIN, "前膝不超过脚尖"),
            GymStep("大腿前侧拉伸", "每组30秒", TrainingPhase.STRETCH),
            GymStep("大腿后侧拉伸", "每组30秒", TrainingPhase.STRETCH),
        )
        TrainingTarget.ABS -> listOf(
            GymStep("猫牛式", "10次", TrainingPhase.WARMUP),
            GymStep("死虫式", "2组 × 10次", TrainingPhase.WARMUP),
            GymStep("卷腹", "4组 × 20次", TrainingPhase.MAIN, "下背贴地，用腹肌发力"),
            GymStep("悬垂举腿", "3组 × 12次", TrainingPhase.MAIN, "控制速度，不要借力摆荡"),
            GymStep("俄罗斯转体", "3组 × 20次", TrainingPhase.MAIN, "双脚离地，核心稳定"),
            GymStep("平板支撑", "3组 × 45秒", TrainingPhase.MAIN, "身体成一条直线"),
            GymStep("腹部拉伸", "30秒", TrainingPhase.STRETCH),
            GymStep("婴儿式放松", "30秒", TrainingPhase.STRETCH),
        )
        TrainingTarget.FAT_LOSS -> listOf(
            GymStep("开合跳", "2分钟", TrainingPhase.WARMUP),
            GymStep("高抬腿", "1分钟", TrainingPhase.WARMUP),
            GymStep("波比跳", "4组 × 10次", TrainingPhase.MAIN, "动作完整，不要偷懒"),
            GymStep("登山者", "4组 × 30秒", TrainingPhase.MAIN, "核心收紧，速度要快"),
            GymStep("跳绳", "4组 × 15次", TrainingPhase.MAIN, "落地轻柔，膝盖缓冲"),
            GymStep("战绳", "4组 × 30秒", TrainingPhase.MAIN, "全身发力，保持节奏"),
            GymStep("全身拉伸", "5分钟", TrainingPhase.STRETCH),
            GymStep("深呼吸放松", "1分钟", TrainingPhase.STRETCH),
        )
    }

    val targetNames = mapOf(
        TrainingTarget.BACK to "练背日",
        TrainingTarget.CHEST to "练胸日",
        TrainingTarget.LEGS to "练腿日",
        TrainingTarget.ABS to "练腹日",
        TrainingTarget.FAT_LOSS to "减脂日"
    )
}
