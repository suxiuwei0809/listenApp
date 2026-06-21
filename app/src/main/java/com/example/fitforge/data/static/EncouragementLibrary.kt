package com.example.fitforge.data.static

/**
 * 鼓励语库 —— 静态数据，不需要数据库。
 * 新增/修改鼓励语只需改这里的代码。
 */
object EncouragementLibrary {

    /** 平板支撑用鼓励语 */
    val plank: List<String> = listOf(
        "准备好挑战自己了吗？",
        "加油！刚开始，保持姿势！",
        "太棒了！已经15秒了！",
        "不要放弃！你可以的！",
        "已经30秒了，继续保持！",
        "太厉害了！坚持就是胜利！",
        "45秒了！你是最棒的！",
        "一分钟了！突破自己！",
        "75秒！马上突破记录了！",
        "90秒！你是王者！",
        "坚持住！你可以的！",
        "再坚持10秒！",
        "核心收紧！不要放弃！",
        "你已经很棒了！继续！",
        "最后一下！加油！",
        "感受核心的力量！",
        "保持呼吸！稳住！",
        "突破自我，就在此刻！",
        "每多一秒，都是进步！",
        "你已经超越昨天的自己了！"
    )

    /** 根据坚持秒数获取对应鼓励语 */
    fun getPlankEncouragement(seconds: Int): String {
        val index = (seconds / 15).coerceAtMost(plank.size - 1)
        return plank[index]
    }

    /** 通用鼓励语 */
    val general: List<String> = listOf(
        "完成训练，坚持就是胜利！",
        "今天的努力，明天的成果！",
        "每一次训练都不辜负自己！",
        "你比自己想象的更强大！",
        "汗水不会撒谎，加油！",
        "自律给我自由！",
        "只有自己才能定义自己的极限！",
        "今天的酸痛，是明天的力量！",
        "放弃很容易，但坚持一定很酷！",
        "你已经很棒了，继续加油！"
    )

    fun getRandom(): String = general.random()
}
