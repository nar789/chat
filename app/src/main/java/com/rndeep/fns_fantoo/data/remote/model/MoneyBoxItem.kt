package com.rndeep.fns_fantoo.data.remote.model

data class MoneyBoxItem(
    var type :String,
)

data class MoneyBoxTopInfo(
    var clubName :String,
    var clubThumbnail : String?,
    var accountAmount : Int,
)

data class MoneyBoxRanking(
    var rank : Int,
    var rankingNickname : String,
    var rankMoney : Int
)

data class MoneyBoxWithDrawItem(
    var month : Int,
    var items : List<MoneyBoxWithDrawListItem>
)

data class MoneyBoxWithDrawListItem(
    var donateNickname : String,
    var donateMoney : Int,
    var donateDate : Long,
    var donateAmount: Int
)