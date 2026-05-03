/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.shub39.romanization

/**
 * A pure Kotlin implementation of a Chinese Pinyin helper.
 */
object Pinyin {
    fun isChinese(ch: Char): Boolean {
        return ch in '\u4E00'..'\u9FA5'
    }

    fun toPinyin(ch: Char): String {
        return PINYIN_MAP[ch] ?: ch.toString()
    }

    // A map of common Chinese characters to their Pinyin (without tones).
    // In a full implementation, this would contain ~20,000 characters.
    // Here we include a substantial set of common characters.
    private val PINYIN_MAP: Map<Char, String> = mapOf(
        '我' to "wo", '你' to "ni", '他' to "ta", '她' to "ta", '它' to "ta",
        '们' to "men", '这' to "zhe", '那' to "na", '哪' to "na", '谁' to "shui",
        '什' to "shen", '么' to "me", '的' to "de", '了' to "le", '在' to "zai",
        '是' to "shi", '有' to "you", '个' to "ge", '和' to "he", '也' to "ye",
        '就' to "jiu", '不' to "bu", '到' to "dao", '说' to "shuo", '要' to "yao",
        '去' to "qu", '来' to "lai", '用' to "yong", '做' to "zuo", '想' to "xiang",
        '看' to "kan", '见' to "jian", '听' to "ting", '说' to "shuo", '读' to "du",
        '写' to "xie", '开' to "kai", '关' to "guan", '大' to "da", '小' to "xiao",
        '多' to "duo", '少' to "shao", '好' to "hao", '坏' to "huai", '新' to "xin",
        '旧' to "jiu", '早' to "zao", '晚' to "wan", '对' to "dui", '错' to "cuo",
        '高' to "gao", '低' to "di", '远' to "yuan", '近' to "jin", '快' to "kuai",
        '慢' to "man", '冷' to "leng", '热' to "re", '美' to "mei", '丑' to "chou",
        '中' to "zhong", '国' to "guo", '人' to "ren", '家' to "jia", '学' to "xue",
        '生' to "sheng", '校' to "xiao", '书' to "shu", '字' to "zi", '话' to "hua",
        '语' to "yu", '文' to "wen", '天' to "tian", '地' to "di", '山' to "shan",
        '水' to "shui", '火' to "huo", '风' to "feng", '月' to "yue", '日' to "ri",
        '年' to "nian", '月' to "yue", '日' to "ri", '时' to "shi", '分' to "fen",
        '秒' to "miao", '点' to "dian", '上' to "shang", '下' to "xia", '前' to "qian",
        '后' to "hou", '左' to "zuo", '右' to "you", '里' to "li", '外' to "wai",
        '一' to "yi", '二' to "er", '三' to "san", '四' to "si", '五' to "wu",
        '六' to "liu", '七' to "qi", '八' to "ba", '九' to "ji", '十' to "shi",
        '百' to "bai", '千' to "qian", '万' to "wan", '亿' to "yi", '零' to "ling",
        '饭' to "fan", '菜' to "cai", '茶' to "cha", '水' to "shui", '酒' to "jiu",
        '衣' to "yi", '服' to "fu", '车' to "che", '路' to "lu", '钱' to "qian",
        '朋' to "peng", '友' to "you", '爱' to "ai", '心' to "xin", '手' to "shou",
        '足' to "zu", '目' to "mu", '口' to "kou", '耳' to "er", '头' to "tou",
        '发' to "fa", '面' to "mian", '色' to "se", '香' to "xiang", '味' to "wei"
        // ... this can be expanded with more characters as needed
    )
}
