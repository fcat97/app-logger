/**
 * {DateTimeUtils}
 * Copyright (C) 2022 github/fCat97
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package media.uqab.libapplogger

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

object DateTimeUtil {
    fun getMonthName(calendar: Calendar): String {
        return getMonthName(calendar[Calendar.MONTH])
    }

    fun getShortMonthName(calendar: Calendar): String {
        return getShortMonthName(calendar[Calendar.MONTH])
    }

    /**
     * Get Month Name
     * @param month index of month in Year. From 0-11
     * @return Full name of Month in English
     */
    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            11 -> "December"
            else -> "January"
        }
    }

    private fun getShortMonthName(month: Int): String {
        return when (month) {
            1 -> "Feb"
            2 -> "Mar"
            3 -> "Apr"
            4 -> "May"
            5 -> "Jun"
            6 -> "Jul"
            7 -> "Aug"
            8 -> "Sep"
            9 -> "Oct"
            10 -> "Nov"
            11 -> "Dec"
            else -> "Jan"
        }
    }

    /**
     * Get a time formatted to string.
     * [timeInMillis] time to format
     * [pattern] pattern to format.
     *
     * See [SimpleDateFormat] doc for available patterns.
     */
    fun formatDateTime(timeInMillis: Long, pattern: String = "d/MM/yy h:m:s:SSS a z"): String {
        return try {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            formatter.format(Date(timeInMillis))
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Convert milliseconds into readable time duration.
     * For instance, 3600_000ms is 1h:0m:0s.
     * @param timeInMillis amount of time in milliseconds
     * @return a readable time duration.
     */
    fun getReadableTime(timeInMillis: Long): String {
        val time = timeInMillis / 1000 // total seconds
        if (time == 0L) return "0s"
        val week = (time / 604800).toInt()
        val day = (time % 604800 / 86400).toInt()
        val hour = (time % 86400 / 3600).toInt()
        val min = time % 3600 / 60
        val sec = time % 60

        var text = ""
        if (week > 0) {
            text += week.toString() + "w:"
        }
        if (day > 0) {
            text += day.toString() + "d:"
        }
        if (hour > 0) {
            text += hour.toString() + "h:"
        }
        if (min > 0) {
            text += if (min < 10) "0$min" else "" + min
            text += "m:"
        }
        if (sec > 0) {
            text += if (sec < 10) "0$sec" else "" + sec
            text += "s:"
        }
        return if (text.isEmpty()) text else text.substring(0, text.length - 1)
    }

    val currentReadableTimeWithMillis: String
        get() { return getReadableEpoch() }
    val thisMonthFirstMidnight: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar[Calendar.DAY_OF_MONTH] = 1
            return getMidnight(calendar.timeInMillis)
        }

    /**
     * Get midnight time i.e. 12AM in millis any date in millis
     * @param currentTime dateTime formatted in millisecond
     * @return starting time of date i.e. 12:00:00:000 AM
     */
    fun getMidnight(currentTime: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = currentTime
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    /**
     * Round a time where minute is multiple of 5
     * @param time time to round
     * @return rounded time
     */
    fun roundTo5min(time: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        var min = calendar[Calendar.MINUTE]
        val rem = min % 5
        min -= rem
        calendar[Calendar.MINUTE] = min
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.timeInMillis
    }

    /**
     * Get Date difference(n * 24h) Between two time
     * @param time_1 first time
     * @param time_2 second time
     * @return +ve difference if time_1 is larger and vice versa.
     */
    fun dayBetween(time_1: Long, time_2: Long): Int {
        val date1 = Calendar.getInstance()
        date1.clear()

        val date2 = Calendar.getInstance()
        date2.clear()

        date1.timeInMillis = time_1
        date2.timeInMillis = time_2
        val dayOfYear1 = date1[Calendar.DAY_OF_YEAR]
        val dayOfYear2 = date2[Calendar.DAY_OF_YEAR]
        return dayOfYear1 - dayOfYear2
    }

    /**
     * Get time before or after with human readable units
     *
     * @param time time from which to calculate.
     * @param unit [TimeUnit] in human readable form.
     * @param amount amount of time to add or subtract.
     * positive [amount] to add and negetive [amount] to subtract.
     *
     * @return Result after calculation.
     */
    fun timeAfter(time: Date, unit: TimeUnit, amount: Long): Date {
        val now = Date()
        when (unit) {
            TimeUnit.DAY -> {
                val x = amount * 24 * 60 * 60 * 1000
                val y = time.time
                now.time = x + y
            }
            TimeUnit.HOUR -> {
                val x = amount * 60 * 60 * 1000
                val y = time.time
                now.time = x + y
            }
            TimeUnit.MINUTE -> {
                val x = amount * 60 * 1000
                val y = time.time
                now.time = x + y
            }
            TimeUnit.SECOND -> {
                val x = amount * 1000
                val y = time.time
                now.time = x + y
            }
            TimeUnit.MILLISECOND -> {
                val y = time.time
                now.time = amount + y
            }
        }
        return now
    }

    /**
     * Get a time formatted as human readable
     */
    fun getReadableEpoch(
        time: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        val calendar = Calendar.getInstance(timeZone)
        calendar.timeInMillis = time

        return formatDateTime(calendar.timeInMillis, "dd MMM yyyy HH:mm:ss:SSS")
    }

    /**
     * Get time Difference between two dates.
     *
     * @param t1 A time in millisecond to which the second one will be compared.
     * @param t2 another time in millisecond.
     *
     *
     * @return natural human readable time difference of [t2] from [t1].
     */
    fun getNaturalDiff(
        t1: Long,
        t2: Long
    ): String {
        val isFuture = t1 - t2 < 0
        val diff  = abs(t1 - t2)

        val time = diff / 1000 // total seconds
        val week = (time / 604800).toInt()
        val day = (time % 604800 / 86400).toInt()
        val hour = (time % 86400 / 3600).toInt()
        val min = time % 3600 / 60

        val suffix = if (isFuture) "" else " ago"
        val prefix = if (isFuture) "after " else ""

        return if (min > 1) {
            val text = when {
                week > 1 -> {
                    "$week weeks"
                }
                week > 0 -> {
                    "a week"
                }
                day > 1 -> {
                    "$day days"
                }
                day > 0 -> {
                    "a day"
                }
                hour > 1 -> {
                    "$hour hours"
                }
                hour > 0 -> {
                    "a hour"
                }
                min > 10 -> {
                    "$min minutes"
                }
                min > 1 -> {
                    "a moment"
                }
                else -> {
                    ""
                }
            }
            prefix + text + suffix
        } else { "just now" }
    }

    enum class TimeUnit {
        DAY, HOUR, MINUTE, SECOND, MILLISECOND
    }

    /**
     * Simple Date Class. Returns today by default.
     */
    data class SimpleDate(
        val year: Int = Calendar.getInstance()[Calendar.YEAR],

        val month: Int = Calendar.getInstance()[Calendar.MONTH + 1],

        val date: Int = Calendar.getInstance()[Calendar.DATE]
    ) {
        /**
         * Get a [Date] object
         */
        fun toDate(): Date {
            val c = Calendar.getInstance()
            c.clear()
            c[Calendar.YEAR] = year
            c[Calendar.MONTH] = (month - 1).coerceAtLeast(0).coerceAtMost(11)
            c[Calendar.DATE] = date.coerceAtLeast(1).coerceAtMost(31)

            return Date(c.timeInMillis)
        }

        /**
         * Return difference of days in between.
         *
         * @param date [SimpleDate] to compare
         * @return amount of difference in days. +ve if [date] is smaller.
         */
        infix fun inFutureFrom(date: SimpleDate): Int {
            return dayBetween(this.toDate().time, date.toDate().time)
        }

        /**
         * Get a future date object.
         *
         * @param date date from which future date will be returned.
         * @return future date.
         */
        infix fun afterDaysOf(days: Int): SimpleDate {
            val c = Calendar.getInstance()
            c.clear()
            c.timeInMillis = this.toDate().time
            c.add(Calendar.DATE, days)

            return SimpleDate(
                year = c[Calendar.YEAR],
                month = c[Calendar.MONTH] + 1,
                date = c[Calendar.DATE]
            )
        }
    }

    /**
     * Week Dates
     */
    enum class WeekDates(val value: Int) {
        SAT(Calendar.SATURDAY),
        SUN(Calendar.SUNDAY),
        MON(Calendar.MONDAY),
        TUE(Calendar.TUESDAY),
        WED(Calendar.WEDNESDAY),
        THU(Calendar.THURSDAY),
        FRI(Calendar.FRIDAY),
    }

    /**
     * A simple wrapper of a starting and ending time
     *
     * @param starting Starting time of [Epoch].
     * @param ending Ending time of [Epoch]
     */
    data class Epoch(
        val starting: Long,
        val ending: Long,
    )

    val now: Long get() = System.currentTimeMillis()
}