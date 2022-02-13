/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-mirai 的一部分。
 *
 *  simbot-component-mirai 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-mirai 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot.mlh

import love.forte.simbot.utils.md5
import net.mamoe.mirai.utils.DeviceInfo
import kotlin.random.Random
import kotlin.random.nextInt


internal fun simbotMiraiDeviceInfo(c: Long, s: Long): DeviceInfo {
    val r = Random(c * s)
    return DeviceInfo(
        display = "DDMO.200122.001".toByteArray(),
        product = "DDMO".toByteArray(),
        device = "DDMO".toByteArray(),
        board = "DDMO".toByteArray(),
        brand = "DDMO".toByteArray(),
        model = "DDMO".toByteArray(),
        bootloader = "unknown".toByteArray(),
        // mamoe/mirai/mirai:10/MIRAI.200122.001/
        fingerprint = "DDMO/DDMO/DDMO:10/DDMO.200122.001/${
            getRandomString(
                7,
                '0'..'9',
                r
            )
        }:user/release-keys".toByteArray(),
        bootId = md5 { digest(getRandomByteArray(16, r)) },
        procVersion = "Linux version 3.0.30-${getRandomString(8, r)} (android-build@xxx.xxx.xxx.xxx.com)".toByteArray(),
        baseBand = byteArrayOf(),
        version = DeviceInfo.Version(),
        simInfo = "T-Mobile".toByteArray(),
        osType = "android".toByteArray(),
        macAddress = "02:00:00:00:00:00".toByteArray(),
        wifiBSSID = "02:00:00:00:00:00".toByteArray(),
        wifiSSID = "<unknown ssid>".toByteArray(),
        imsiMd5 = md5 { digest(getRandomByteArray(16, r)) },
        imei = getRandomString(15, '0'..'9', r),
        apn = "wifi".toByteArray()

    )
}


/*
 * 以下源代码修改自
 * net.mamoe.mirai.utils.SystemDeviceInfo.kt、
 * net.mamoe.mirai.utils.ExternalImage.kt
 *
 * 原源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

/**
 * 生成长度为 [length], 元素为随机 `0..255` 的 [ByteArray]
 */
@JvmSynthetic
internal fun getRandomByteArray(length: Int, r: Random): ByteArray = ByteArray(length) { r.nextInt(0..255).toByte() }

/**
 * 随机生成长度为 [length] 的 [String].
 */
@JvmSynthetic
internal fun getRandomString(length: Int, r: Random): String =
    getRandomString(length, r, *defaultRanges)

@JvmSynthetic
internal val defaultRanges: Array<CharRange> = arrayOf('a'..'z', 'A'..'Z', '0'..'9')

/**
 * 根据所给 [charRange] 随机生成长度为 [length] 的 [String].
 */
@JvmSynthetic
internal fun getRandomString(length: Int, charRange: CharRange, r: Random): String =
    String(CharArray(length) { charRange.random(r) })

/**
 * 根据所给 [charRanges] 随机生成长度为 [length] 的 [String].
 */
@JvmSynthetic
internal fun getRandomString(length: Int, r: Random, vararg charRanges: CharRange): String =
    String(CharArray(length) { charRanges[r.nextInt(0..charRanges.lastIndex)].random(r) })

@JvmSynthetic
internal fun generateUUID(md5: ByteArray): String {
    return "${md5[0, 3]}-${md5[4, 5]}-${md5[6, 7]}-${md5[8, 9]}-${md5[10, 15]}"
}

@JvmSynthetic
internal operator fun ByteArray.get(rangeStart: Int, rangeEnd: Int): String = buildString {
    for (it in rangeStart..rangeEnd) {
        append(this@get[it].fixToString())
    }
}

@JvmSynthetic
internal fun Byte.fixToString(): String {
    return when (val b = this.toInt() and 0xff) {
        in 0..15 -> "0${this.toString(16).uppercase()}"
        else -> b.toString(16).uppercase()
    }
}

