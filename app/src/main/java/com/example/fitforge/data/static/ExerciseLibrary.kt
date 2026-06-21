package com.example.fitforge.data.static

/**
 * 动作库静态数据 —— 不需要 Room 数据库，直接定义在内存中。
 * 新增/修改动作只需改这里的 Kotlin 代码。
 */
data class Exercise(
    val name: String,
    val cat: String,
    val sets: String,
    val tips: List<String>,
    val caution: String
)

object ExerciseLibrary {

    val catNames = mapOf(
        "chest" to "胸部",
        "back" to "背部",
        "legs" to "腿部",
        "abs" to "腹部",
        "shoulder" to "肩部",
        "arms" to "手臂"
    )

    val catIcons = mapOf(
        "chest" to "🏋",
        "back" to "🏋",
        "legs" to "🦵",
        "abs" to "🔥",
        "shoulder" to "🏋",
        "arms" to "💪"
    )

    val data: Map<String, List<Exercise>> = mapOf(
        "chest" to listOf(
            Exercise("平板杠铃卧推", "胸部", "4组×10次",
                listOf("肩胛骨收紧贴凳", "下放至胸口位置", "推起时不要锁死肘关节", "双脚踏实地面稳定身体"),
                "重量循序渐进，切莫盲目加码"),
            Exercise("上斜哑铃卧推", "胸部", "4组×10次",
                listOf("凳面角度30-45度", "下放时肘部成90度", "推起时哑铃微微靠拢", "感受上胸发力"),
                "控制哑铃轨迹，避免肩部代偿"),
            Exercise("蝴蝶机夹胸", "胸部", "3组×12次",
                listOf("挺胸收腹", "顶峰收缩停顿1秒", "缓慢回放", "不要用手臂发力"),
                "重量不宜过大，注重收缩感")
        ),
        "back" to listOf(
            Exercise("引体向上", "背部", "4组×8次",
                listOf("核心收紧", "肩胛骨下沉发力", "下巴过杆", "缓慢下放"),
                "不要借力摆荡，做不到可用弹力带辅助"),
            Exercise("杠铃划船", "背部", "4组×10次",
                listOf("俯身约45度", "背部发力拉向腹部", "肘部贴身", "顶峰收缩2秒"),
                "保持腰部中立，避免弓腰"),
            Exercise("坐姿下拉", "背部", "3组×12次",
                listOf("挺胸", "下拉至锁骨", "肩胛骨下回旋", "缓慢回放"),
                "不要过度后仰借力")
        ),
        "legs" to listOf(
            Exercise("杠铃深蹲", "腿部", "5组×8次",
                listOf("膝盖对准脚尖方向", "蹲至大腿平行地面", "重心在脚掌中后部", "核心收紧保持稳定"),
                "腰部必须保持中立，切勿弓腰"),
            Exercise("罗马尼亚硬拉", "腿部", "4组×10次",
                listOf("臀部向后推", "杠铃沿腿面下滑", "感受腘绳肌拉伸", "髋关节铰链运动"),
                "背部保持平直，不可弓背")
        ),
        "abs" to listOf(
            Exercise("卷腹", "腹部", "4组×20次",
                listOf("下背始终贴地", "用腹肌卷起上半身", "不需要起太高", "双手放耳侧不要抱头"),
                "切莫双手抱头拉扯颈部"),
            Exercise("悬垂举腿", "腹部", "3组×12次",
                listOf("控制速度", "骨盆后倾", "举至与地面平行", "不要借力摆荡"),
                "握力不足可使用助力带")
        ),
        "shoulder" to listOf(
            Exercise("杠铃推举", "肩部", "4组×10次",
                listOf("核心收紧", "推至头顶正上方", "下放至锁骨位置", "不要过度后仰"),
                "肩关节有不适立即停止"),
            Exercise("侧平举", "肩部", "3组×15次",
                listOf("小臂微倾", "举至与肩同高", "小指侧略高", "控制速度"),
                "不要耸肩，重量不宜过大")
        ),
        "arms" to listOf(
            Exercise("杠铃弯举", "手臂", "3组×12次",
                listOf("大臂贴紧身体", "只动前臂", "顶峰收缩", "缓慢下放"),
                "不要借力甩举"),
            Exercise("绳索下压", "手臂", "3组×12次",
                listOf("大臂固定", "只动前臂", "完全伸直时挤压", "缓慢回放"),
                "手肘不要外展")
        )
    )

    /** 根据关键词模糊搜索动作 */
    fun search(keyword: String): List<Exercise> {
        if (keyword.isBlank()) return emptyList()
        val kw = keyword.trim().lowercase()
        return data.values.flatten().filter {
            it.name.contains(kw, ignoreCase = true) ||
            it.cat.contains(kw, ignoreCase = true)
        }
    }

    /** 按部位获取动作列表 */
    fun getByCategory(category: String): List<Exercise> = data[category] ?: emptyList()

    /** 所有部位 */
    val categories: Set<String> get() = data.keys
}
