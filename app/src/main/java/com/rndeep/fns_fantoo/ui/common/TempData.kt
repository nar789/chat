package com.rndeep.fns_fantoo.ui.common

import com.rndeep.fns_fantoo.data.local.model.BannerItem
import com.rndeep.fns_fantoo.data.local.model.CurationDataItem
import com.rndeep.fns_fantoo.data.local.model.TrendTagItem
import com.rndeep.fns_fantoo.data.remote.model.HomeAlarmData
import com.rndeep.fns_fantoo.data.remote.model.RecommendationClub

//트렌트 아이템 으로 가독성을 위해 임시로 만들어 둠
object TempData {
    val tempStrings = arrayListOf(
        TrendTagItem("1", "박보검"),
        TrendTagItem("2", "이준호X임윤아"),
        TrendTagItem("3", "LeeJunHo"),
        TrendTagItem("4", "방탄소년단_지민"),
        TrendTagItem("5", "하나빼기"),
        TrendTagItem("6", "쌍꺼풀 유무"),
        TrendTagItem("7", "킹더랜드"),
        TrendTagItem("8", "마음당_자기소개"),
        TrendTagItem("9", "원피스"),
        TrendTagItem("10", "써스데이아일랜드원피스"),
        TrendTagItem("11", "반팔티"),
        TrendTagItem("12", "블라우스"),
        TrendTagItem("13", "여름원피스"),
        TrendTagItem("14", "롱원피스"),
        TrendTagItem("15", "팬츠"),
        TrendTagItem("16", "여성점프수트"),
        TrendTagItem("17", "선크림"),
        TrendTagItem("18", "마녀공장"),
        TrendTagItem("19", "제이숲바디워시"),
        TrendTagItem("20", "제이숲샴푸"),
        TrendTagItem("21", "멀티밤"),
        TrendTagItem("22", "수분크림"),
        TrendTagItem("23", "헤어팩"),
        TrendTagItem("24", "제이숲샴푸바"),
        TrendTagItem("25", "날씨"),
        TrendTagItem("26", "장마"),
        TrendTagItem("27", "관련주"),
        TrendTagItem("28", "테마주"),
        TrendTagItem("29", "수혜주"),
        TrendTagItem("30", "주식"),
        TrendTagItem("31", "여름"),
        TrendTagItem("32", "비"),
        TrendTagItem("33", "기상청"),
        TrendTagItem("34", "재테크"),
        TrendTagItem("35", "경제"),
        TrendTagItem("36", "투자")

    )

    val tempAlarmDatas = listOf<HomeAlarmData>(
        HomeAlarmData(
            1,
            "replayComment",
            null,
            "null",
            "회원 닉네임님 외 3명이 회원님의 글에 댓글을 남겼습니다. ",
            null,
            "커뮤니티/카테고리",
            "2분",
            false
        ),
        HomeAlarmData(
            2,
            "message",
            null,
            "null",
            "발송 타이틀",
            null,
            "발송 내용",
            "2분",
            true
        ),
        HomeAlarmData(
            3,
            "alert",
            null,
            "null",
            "클럽장에 의해 회원님의 댓글이 삭제되었습니다.",
            "세상에서 가장 소름돋는 라이브",
            "BTS 팬틀럽",
            "2분",
            true
        ),
        HomeAlarmData(
            4,
            "alert",
            null,
            "null",
            "관리자에 의해 회원님의 글이 삭제되었습니다.",
            "세상에서 가장 소름돋는 라이브",
            "커뮤니티/카테고리",
            "2분",
            false,
        ),
        HomeAlarmData(
            5,
            "alert",
            null,
            "null",
            "신고에 의해 회원님의 댓글이 삭제되었습니다.",
            "세상에서 가장 소름돋는 라이브",
            "커뮤니티/카테고리",
            "2분",
            true
        ),
        HomeAlarmData(
            6,
            "clubAlert",
            null,
            "null",
            "클럽 가입이 승인되었습니다.",
            null,
            "BTS 팬틀럽",
            "2분",
            false
        ),
        HomeAlarmData(
            7,
            "clubAlert",
            null,
            "null",
            "클럽장 위임이 완료되어 클럽장이 되었습니다.",
            null,
            "BTS 팬틀럽",
            "2분",
            false
        ),
        HomeAlarmData(
            8,
            "",
            null,
            "null",
            "클럽장으로 위임 받았습니다. 수락 여부를 선택해 주세요!",
            null,
            "BTS 팬틀럽",
            "2분",
            true
        ),


    )

    val tempBannerList = arrayListOf<BannerItem>(
        BannerItem(null,"http://kdg.fantoo.co.kr:5000/uploads_img/1645581814932.jpg", "http://webview.fantoo.co.kr/fanit"),
        BannerItem(null,"http://kdg.fantoo.co.kr:5000/uploads_img/1646023370193.jpg", "http://webview.fantoo.co.kr/event/blankreplyevent",),
        BannerItem(null,"http://kdg.fantoo.co.kr:5000/uploads_img/1642749179530.jpg", "hhttp://m.fantoo.co.kr/event/reviewevent_v3/",),
        BannerItem(null,"http://kdg.fantoo.co.kr:5000/uploads_img/1644908182392.jpg", "http://webview.fantoo.co.kr/contest/2021",),
        BannerItem(null,"http://kdg.fantoo.co.kr:5000/uploads_img/1642145886339.jpg", "http://m.fantoo.co.kr/event/singerevent1month/",),
        BannerItem(null,"http://img.fantoo.co.kr/uploads/honor_1026.png", "http://webview.fantoo.co.kr/event/honorevent"),

    )

    val tempCurationList = arrayListOf<CurationDataItem>(
        CurationDataItem(
            id = "1",
            curationText = "첫번째",
            curationImage = "http://kdg.fantoo.co.kr:5000/uploads_img/1645581814932.jpg"
        ),
        CurationDataItem(
            id = "2",
            curationText = "다들 이것 좀 보시오!",
            curationImage = "http://kdg.fantoo.co.kr:5000/uploads_img/1646023370193.jpg"
        ),
        CurationDataItem(
            id = "3",
            curationText = "아직 5월 6일이네",
            curationImage = "http://kdg.fantoo.co.kr:5000/uploads_img/1642749179530.jpg"
        ),
        CurationDataItem(
            id = "4",
            curationText = "7월까지 다 만들 수 있으려나~~",
            curationImage = "http://kdg.fantoo.co.kr:5000/uploads_img/1644908182392.jpg"
        ),
        CurationDataItem(
            id = "5",
            curationText = "여름여름여름",
            curationImage = "http://kdg.fantoo.co.kr:5000/uploads_img/1642145886339.jpg"
        )
    )

    val tempRecommendClubList = listOf(
        RecommendationClub("1","testClub",true, arrayListOf("태그1","태그2","태그3"),"",0),
        RecommendationClub("2","testClub2",true, arrayListOf("태그1","태그2","태그3"),"https://imagedelivery.net/peZXyXogT-VgnN1sGn2T-Q/e7b6781e-c5f8-4075-bf7c-2e92b61a0500/public",0),
        RecommendationClub("3","testClub3",true, arrayListOf("태그1","태그2","태그3"),"",0),
        RecommendationClub("4","testClub4",true, arrayListOf("태그1","태그2","태그3"),"https://imagedelivery.net/peZXyXogT-VgnN1sGn2T-Q/eb47b2f2-60cb-4456-91ef-8b4c4761c000/thumbnail",0),
        RecommendationClub("5","testClub5",true, arrayListOf("태그1","태그2","태그3"),"",0),
        RecommendationClub("6","testClub6",true, arrayListOf("태그1","태그2","태그3"),"",0),
        RecommendationClub("7","testClub7",true, arrayListOf("태그1","태그2","태그3"),"https://imagedelivery.net/peZXyXogT-VgnN1sGn2T-Q/eb47b2f2-60cb-4456-91ef-8b4c4761c000/thumbnail",0),
        )


}