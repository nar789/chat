package com.rndeep.fns_fantoo.utils

import com.rndeep.fns_fantoo.ui.login.SocialInfo
import java.security.Key

object ConstVariable {

    const val BASE_URL = "https://fauth.fantoo.co.kr:8121"
    const val BASE_API_URL = "https://fapi.fantoo.co.kr:9121"
    const val CLOUDFLARE_URL = "https://api.cloudflare.com"
    const val TRANSLATE_URL = "http://ntrans.fantoo.co.kr:5000"

    //카테고리 초기 설정값
    const val CURRENT_NOTING = "noting"

    //게시글 보여주는 방식 Type
    const val POST_TYPE_FEED = "Feed"
    const val POST_TYPE_LIST = "List"

    //Post의 보여주는 위치에 따른 viewType
    const val VIEW_HOME_MAIN = "homeMain"
    const val VIEW_COMMUNITY_MAIN = "communityMain"
    const val VIEW_COMMUNITY_SEARCH = "communitySearch"
    const val VIEW_COMMUNITY_PAGE_COMMON = "communityPageCommon"
    const val VIEW_COMMUNITY_PAGE_HOT = "communityPageHot"
    const val VIEW_COMMUNITY_BOOKMARK = "communityBookmarkPost"
    const val VIEW_COMMUNITY_MY = "communityMyPost"
    const val VIEW_CLUB_MAIN = "clubMain"
    const val VIEW_CLUB_PAGE_HOME = "clubPage_home"
    const val VIEW_CLUB_SEARCH = "clubPageSearch"
    const val VIEW_CLUB_FREE_BOARD = "clubPage_freeboard"
    const val VIEW_CLUB_ARCHIVE_POST = "clubPage_archive_post"

    //Board Type 들
    const val BOARD_NOTICE_TYPE = "boardNotice"
    const val BOARD_COMMON_TYPE = "boardCommon"
    const val BOARD_POST_PAGE = "boardPostPage"
    const val BOARD_POST_PAGE_SEARCH = "boardPostPageSearch"
    const val BOARD_POST_MAIN_SEARCH = "boardPostMainSearch"

    //추천 클럽 카테고리 관련
    const val RECOMMEND_CLUB_USE_CATEGORY = "useCategory"
    const val RECOMMEND_CLUB_DETAIL_FREE_BOARD = "recommendClubFreeBoard"
    const val RECOMMEND_CLUB_MAIN = "recommendClubMain"

    //ViewHolder의 타입들
    const val TYPE_BANNER = "BANNER"
    const val TYPE_CLUB = "club"
    const val TYPE_CLUB_TO_REPLY = "clubToReply"
    const val TYPE_COMMUNITY = "community"
    const val TYPE_COMMUNITY_TO_REPLY = "communityToReply"
    const val TYPE_IS_NOT_LOGIN = "isNotLogin"
    const val TYPE_IS_NOT_PROFILE = "isNotProfile"
    const val TYPE_NO_LIST = "isNoList"
    const val TYPE_HOME_RECOMMEND_CLUB = "homeRecommendClub"
    const val TYPE_POPULAR_CURATION = "popularCuration"
    const val TYPE_AD = "ad"
    const val TYPE_BOARD = "BOARD"
    const val TYPE_COMMUNITY_NOTICE = "COMMUNITYNOTICE"
    const val TYPE_CLUB_NOTICE = "CLUBNOTICE"
    const val TYPE_REALTIME = "REALTIME"
    const val TYPE_WEEK_TOP = "WEEKTOP"
    const val TYPE_MORE = "MORE"
    const val TYPE_COMMUNITY_MY_POST = "CommunityMyPost"
    const val TYPE_COMMUNITY_MY_SAVE = "CommunityMySave"
    const val TYPE_MY_CLUB = "myClub"
    const val TYPE_CLUB_CHALLENGE = "clubChallenge"
    const val TYPE_CLUB_TOP_10 = "clubTop10"
    const val TYPE_CLUB_HOT_RECOMMEND = "hotRecommend"
    const val TYPE_CLUB_NEW_RECOMMEND = "newRecommend"

    //Challenge
    const val CHALLENGE_NO_ITEM = "noChallenge"
    const val CHALLENGE_YES = "Challenge"

    //LIKE DISLIKE TYPE
    const val LIKE_CLICK = "like"
    const val DISLIKE_CLICK = "dislike"
    const val LIKE_DISLIKE_TYPE_POST = "post"
    const val LIKE_DISLIKE_TYPE_REPLY = "reply"

    //DB 의 타입들
    const val DB_HOMECATEGORY = "HomeCategory"
    const val DB_POPULARCATEGORY = "PopularCategory"
    const val DB_TRENDPOST = "trendPost"
    const val DB_COMMUNITYPAGE = "CommunityPage"
    const val DB_COMMUNITY_BOARD_PAGE = "CommunityBoardPage"
    const val DB_COMMUNITY_BOOKMARK_POST = "CommunityBookmarkPost"
    const val DB_COMMUNITY_MY_POST = "CommunityMyPost"
    const val DB_CLUBPAGE = "ClubPage"
    const val DB_CLUBPAGE_HOT = "ClubPageHot"
    const val DB_CLUBPAGE_NEW = "ClubPageNew"
    const val DB_CLUB_DETAIL_HOME = "ClubDetailHome"
    const val DB_CLUB_DETAIL_FREEBOARD = "ClubDetailFreeBoard"
    const val DB_CLUB_DETAIL_ARCHIVE = "ClubDetailArchive"
    const val DB_CLUB_DETAIL_ARCHIVE_COMMON = "ClubDetailArchiveCommon"
    const val DB_CLUB_DETAIL_MONEY_BOX = "ClubDetailMoneyBox"
    const val DB_NOTING = "noting"

    //금고 타입
    const val MONEYBOXINFO = "moneyBoxInfo"
    const val MONTHLYRANKING = "monthlyRanking"
    const val WITHDRAWLIST = "withdrawList"

    //BottomSheetType
    const val ClubCreateSettingAcceptMethod = 0
    const val ClubCreateSettingClubOpen = 1
    const val ClubCreateSettingMajorLang = 2
    const val ClubCreateSettingMajorCountry = 3

    const val KEY_HEADER_LANG = "accept-language"
    const val KEY_ACCESS_TOKEN = "access_token"


    const val KEY_LOGIN_ID = "loginId"
    const val KEY_LOGIN_PASSWORD = "loginPw"
    const val KEY_LOGIN_TYPE = "loginType"
    const val KEY_UID = "integUid"
    const val KEY_TARGET_UID = "targetIntegUid"

    const val RESULT_SUCCESS_CODE = "200"

    const val VALUE_YES = "Y"
    const val VALUE_NO = "N"

    const val DATABASE_NAME = "fantoo-db"

    const val KEY_IMAGE_URL = "imageUrl"

    //아카이브 타입
    const val ARCHIVEIMAGETYPE = "archiveImage"
    const val ARCHIVEPOSTTYPE = "archivePost"

    //Error 상태
    const val ERROR_LIKE_CLICK = "20043"
    const val ERROR_AUTH = "401"
    const val ERROR_NOT_MEMBER = "2000"
    const val ERROR_IMAGE_UPLOAD_FAIL = "IM4004"
    const val ERROR_NO_CATEGORY_CLUB = "CL100"
    const val ERROR_WAIT_FOR_SECOND = "2001"
    const val ERROR_TRANSLATE = "8001"
    const val ERROR_NETWORK = "6666"
    const val ERROR_INTERNAL_SERVER = "500"
    const val ERROR_PLEASE_LATER_LOGIN = "2002"
    const val ERROR_POST_UPDATE_FAIL = "2003"
    const val ERROR_FE1007 = "FE1007"
    const val ERROR_FE1008 = "FE1008"
    const val ERROR_FE2025 = "FE2025"
    const val ERROR_FE3000 = "FE3000"
    const val ERROR_FE3005 = "FE3005"
    const val ERROR_FE3007 = "FE3007"
    const val ERROR_FE3015 = "FE3015"
    const val ERROR_AE5000 = "AE5000"
    const val ERROR_AE5002 = "AE5002"
    const val ERROR_AE5007 = "AE5007"
    const val ERROR_FE3045 = "FE3045"

    //Adapter Payload
    const val PAYLOAD_LIKE_CLICK = "likeClick"
    const val PAYLOAD_HONOR_CLICK = "honorClick"
    const val PAYLOAD_BLOCK_PIECE_CLICK = "blockPieceItem"
    const val PAYLOAD_TRANSLATE_CLICK = "translateClick"
    const val PAYLOAD_REPLY_LIKE_CLICK = "replyLikeClick"
    const val PAYLOAD_REPLY_DISLIKE_CLICK = "replyDisLikeClick"
    const val PAYLOAD_COMMENT_BLOCK_CLICK = "commentBlockClick"

    const val PASSWORD_REGEX =
        "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z0-9$@$!%*#?&]{8,20}$"

    class Config {
        companion object {
            const val KEY_DEVICE_TYPE = "deviceType"
            const val VALUE_DEVICE_TYPE = "android"

            //common header
            const val KEY_HEADER_REFERER = "referer"
            const val KEY_HEADER_USER_AGENT = "user-agent"
            const val VALUE_HEADER_USER_AGENT = "fantoo-android"

            //common param
            const val KEY_COMMON_PARAM_APP_VERSION = "appVersion"
            const val KEY_COMMON_PARAM_DEVICE = "device"
            const val KEY_COMMON_PARAM_OS_VERSION = "osVersion"
            const val KEY_COMMON_PARAM_COUNTRY = "countryCode"

            const val SERVICE_TYPE_PROD = "prod"
            const val SERVICE_TYPE_DEV = "dev"

            const val FEATURE_KDG = false
            const val FEATURE_HONOR = false
        }
    }

    class INTENT {
        companion object {
            const val EXTRA_REGISTER_USER = "registerUser"
            const val EXTRA_COUNTRY = "country"
            const val EXTRA_FROM_ACTIVITY = "fromActivity"
            const val EXTRA_LOGIN_ID = "loginId"
            const val EXTRA_LOGIN_TYPE = "loginType"
            const val EXTRA_NAV_START_DESTINATION_ID = "navStartDestinationId"

            const val EXTRA_MYPROFILE_COUNTRY_ISO2 = "myprofile_country_iso2"
            const val EXTRA_MYPROFILE_NICK_NAME = "myprofile_nickname"
        }
    }

    class ClubSetting {
        companion object {
            const val KEY_MEMBERS_ENTRY_ROUTE_TYPE = "membersScreenEnterType"
            const val KEY_NOTI_DELEGATE_DIALOG = "showDelegateDialog"
        }

        enum class MembersEntryRouteType {
            MEMBER_MANAGEMENT,
            CLUB_DELEGATE
        }
    }

    class ClubDetail {
        companion object {
            const val KEY_POST_URL = "postUrl"
            const val KEY_DETAIL_CLUB_ID = "clubID"
            const val KEY_CATEGORY_CODE = "categoryCode"
        }
    }

    class UserInfo {
        companion object {
            const val KEY_NICKNAME = "nickname"
        }
    }

    class ProfileDef {
        companion object {
            const val NICKNAME_MIN_LENGTH = 2
            const val NICKNAME_MAX_LENGTH = 20
            const val NICKNAME_CHANGE_DATE_LIMIT = 30

            const val PROFILE_PHOTO_MAX_SIZE = 5242880
        }
    }

    class ClubDef {
        companion object {
            const val CLUB_NAME_MIN_LENGTH = 2
            const val CLUB_NAME_MAX_LENGTH = 30
            const val CLUB_NAME_CHANGE_DATE_LIMIT = 90
            const val CLUB_FREEBOARD_OPEN_WORD_MAX_LENGTH = 8
            const val CLUB_SEARCH_WORD_MIN_LENGTH = 2
            const val CLUB_SEARCH_WORD_MAX_LENGTH = 15
            const val CLUB_SEARCH_WORD_MAX_ITEM_COUNT = 10
            const val CLUB_FREEBOARD_OPEN_WORD_MAX_ITEM_COUNT = 10
            const val CLUB_INTRODUCE_LENGTH_LIMIT = 100
            const val CLUB_BOARD_NAME_LENGTH_LIMIT = 20
            const val CLUB_CLOSE_REASON_MIN_LENGTH = 2
            const val CLUB_CLOSE_REASON_MAX_LENGTH = 200
            const val CLUB_DELEGATE_TAGET_MEMBER_AT_LEASET_VISIT_DATE = 14
            const val CLUB_LIST_REQUEST_SIZE = 20

            const val CLUB_PHOTO_MAX_SIZE = 5242880

            const val KEY_CLUB_CATEGORY_ITEM = "clubCategoryItem"
            const val KEY_CLUB_INFO_CLUBID = "clubId"
            const val KEY_CLUB_INFO_MEMBERID = "memberId"
            const val KEY_CLUB_INFO_INTRODUCTION = "introduction"
            const val KEY_CLUB_INFO_MEMBERCOUNT_OPENYN = "memberCountOpenYn"
            const val KEY_CLUB_INFO_MEMBERLIST_OPENYN = "memberListOpenYn"
            const val KEY_CLUB_INFO_OPENYN = "openYn"
            const val KEY_CLUB_INFO_INTEREST_CATEGORY_ID = "interestCategoryId"
            const val KEY_CLUB_INFO_LANGUAGECODE = "languageCode"
            const val KEY_CLUB_INFO_ACTIVE_COUNTRYCODE = "activeCountryCode"
            const val KEY_CLUB_INFO_MEMBER_JOIN_AUTOYN = "memberJoinAutoYn"
            const val KEY_CLUB_INFO_PROFILE_IMAGE = "profileImg"
            const val KEY_CLUB_INFO_BG_IMAGE = "bgImg"
            const val KEY_CLUB_INFO_MEMBER = "clubMemberInfo"
            const val KEY_CLUB_LIST_SIZE = "size"
            const val KEY_CLUB_LIST_NEXTID = "nextId"
            const val KEY_CLUB_LIST_KEYWORD = "keyword"

            //const val CLUB_MEMBERSHIP_LV_GENENAL = 0~19
            const val CLUB_MEMBERSHIP_LV_MANAGER = 20
        }
    }

    class Regist {
        companion object {

            const val KEY_AUTHCODE = "authCode"

            const val KEY_JOIN_AD_AGREE = "adAgreeChoice"
            const val KEY_JOIN_AGE_AGREE = "ageAgree"
            const val KEY_JOIN_COUNTRY = "countryIsoTwo"

            const val KEY_JOIN_SERVICE_AGREE = "serviceAgree"
            const val KEY_JOIN_TEENAGER_AGREE = "teenagerAgree"
            const val KEY_JOIN_REFERRAL_CODE = "useReferralCode"
            const val KEY_JOIN_USERINFO_AGREE = "userInfoAgree"
            const val KEY_USER_NICK = "userNick"

            const val KEY_POLICY_ENTRY_ROUTE_TYPE = "policyEntryRouteType"

            const val KEY_NAV_ARG_INPUT_EMAIL = "registInputEmail"
            const val KEY_NAV_ARG_PASSWORD = "registPassword"
        }

        enum class PolicyType {
            TYPE_USE_SERVICE,
            TYPE_PRIVATE_TREATMENT,
            TYPE_YOUTH_CARE,
            //TYPE_EVENT_RECEIVE
        }
    }

    class Auth {
        companion object {

            const val KEY_CLIENT_ID = "clientId"
            const val VALUE_CLIENT_ID = "o2w7wixc5h7f6mrctn3yjy4a4g0m7hjk"

            const val KEY_RESPONSE_TYPE = "responseType"
            const val VALUE_RESPONSE_TYPE = "code"

            const val KEY_CLIENT_SECRET = "clientSecret"
            const val VALUE_CLIENT_SECRET =
                "j7c7zjgjoiyima92sf14dl2exjll6xzeffuiqsmsovze93jb68nuwet0g5bnl1fl"

            const val KEY_GRANT_TYPE = "grantType"
            const val VALUE_GRANT_TYPE_AUTH = "authorization_code"
            const val VALUE_GRANT_TYPE_REFRESH_TOKEN = "refresh_token"

            const val REFRESH_TOKEN_EXPIRE_DAY = 60

            const val KEY_REFRESH_TOKEN = "refresh_token"

            const val KEY_STATE = "state"
            const val KEY_AUTHCODE = "authCode"

            const val RESULT_KEY_IS_USER = "isUser"
        }
    }

    class Time {
        companion object {
            const val TIME_ZONE_SEOUL = "Asia/Seoul"
        }
    }

    enum class BlockType(val blockType: String) {
        NONE("0"),
        MEMBER("1"),
        POST("2"),
        COMMENT("3");

        companion object {
            fun compare(text: String?): BlockType {
                return try {
                    valueOf(text?:"0")
                } catch (e: Exception) {
                    NONE
                }
            }
        }
    }

    enum class CommentStatus {//댓글 상태 (0:정상, 1:신고, 2:삭제
        NONE,
        REPORTED,
        DELETED;
        companion object {
            fun compare(status: Int): CommentStatus {
                return when(status){
                    REPORTED.ordinal -> REPORTED
                    DELETED.ordinal -> DELETED
                    else -> NONE
                }
            }
        }
    }

    enum class ClubJoinStatus{
        NOT_MEMBER,
        JOINED,
        REQUESTING_JOIN,
        BLOCKED_JOIN;
        companion object {
            fun compare(status: Int): ClubJoinStatus {
                return when(status){
                    NOT_MEMBER.ordinal -> NOT_MEMBER
                    JOINED.ordinal -> JOINED
                    REQUESTING_JOIN.ordinal -> REQUESTING_JOIN
                    BLOCKED_JOIN.ordinal -> BLOCKED_JOIN
                    else -> NOT_MEMBER
                }
            }
        }
    }

    object Fantoo {
        const val STORE_URL = "https://play.google.com/store/apps/details?id=com.rndeep.fns_fantoo"
    }

    object LoginType {
        const val LINE = SocialInfo.TYPE_LINE
        const val FACEBOOK = SocialInfo.TYPE_FACEBOOK
        const val KAKAO = SocialInfo.TYPE_KAKAO
        const val GOOGLE = SocialInfo.TYPE_GOOGLE
        const val TWITTER = SocialInfo.TYPE_TWITTER
        const val APPLE = SocialInfo.TYPE_APPLE
        const val EMAIL = "email"
    }

    object CloudFlare {
        const val IMAGEAPIKEY = "Bearer etE-IoTh7y1CSbqKVbmxcaUxA9WUl0ehAsl0USqt"
        const val VIDEOAPIKET = "Bearer yPZzSlLTXamgT31vTZTWs3--usOENNCw5JEjgpeu"

        const val IMAGE_PRE_URL = "https://imagedelivery.net/peZXyXogT-VgnN1sGn2T-Q/"
        const val IMAGE_OPT_PUBLIC = "/public"
        const val IMAGE_OPT_THUMBNAIL = "/thumbnail"
    }

    //게시판 정렬 타입
    const val SORTFANTOO = "sortFantoo"
    const val SORTPOPULAR = "sortPopular"
}