package org.panta.misskey_for_android_v2.constant

enum class TimelineTypeEnum{
    GLOBAL, HOME, LOCAL, SOCIAL, DESCRIPTION;
    companion object {
        fun toEnum(st: String): TimelineTypeEnum{
            return when(st.toUpperCase()){
                TimelineTypeEnum.GLOBAL.name -> TimelineTypeEnum.GLOBAL
                TimelineTypeEnum.HOME.name -> TimelineTypeEnum.HOME
                TimelineTypeEnum.LOCAL.name -> TimelineTypeEnum.LOCAL
                TimelineTypeEnum.SOCIAL.name -> TimelineTypeEnum.SOCIAL
                TimelineTypeEnum.DESCRIPTION.name -> TimelineTypeEnum.DESCRIPTION
                else -> TimelineTypeEnum.HOME
            }
        }
    }
}

