package com.ekistamp.app.data.translation

object PrefectureTranslator {

    private val names = mapOf(
        "北海道" to "Hokkaido", "青森県" to "Aomori", "岩手県" to "Iwate", "宮城県" to "Miyagi",
        "秋田県" to "Akita", "山形県" to "Yamagata", "福島県" to "Fukushima",
        "茨城県" to "Ibaraki", "栃木県" to "Tochigi", "群馬県" to "Gunma",
        "埼玉県" to "Saitama", "千葉県" to "Chiba", "東京都" to "Tokyo", "神奈川県" to "Kanagawa",
        "新潟県" to "Niigata", "富山県" to "Toyama", "石川県" to "Ishikawa", "福井県" to "Fukui",
        "山梨県" to "Yamanashi", "長野県" to "Nagano", "岐阜県" to "Gifu", "静岡県" to "Shizuoka",
        "愛知県" to "Aichi", "三重県" to "Mie",
        "滋賀県" to "Shiga", "京都府" to "Kyoto", "大阪府" to "Osaka", "兵庫県" to "Hyogo",
        "奈良県" to "Nara", "和歌山県" to "Wakayama",
        "鳥取県" to "Tottori", "島根県" to "Shimane", "岡山県" to "Okayama", "広島県" to "Hiroshima",
        "山口県" to "Yamaguchi",
        "徳島県" to "Tokushima", "香川県" to "Kagawa", "愛媛県" to "Ehime", "高知県" to "Kochi",
        "福岡県" to "Fukuoka", "佐賀県" to "Saga", "長崎県" to "Nagasaki", "熊本県" to "Kumamoto",
        "大分県" to "Oita", "宮崎県" to "Miyazaki", "鹿児島県" to "Kagoshima", "沖縄県" to "Okinawa"
    )

    /** Returns null for unknown values so the caller can fall back to ML Kit. */
    fun toEnglish(ja: String): String? = names[ja.trim()]
}
