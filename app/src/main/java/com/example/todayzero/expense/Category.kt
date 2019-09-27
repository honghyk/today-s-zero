package com.example.todayzero.expense

import com.example.todayzero.R

class Category(var category_string:String, var category_image:Int) {
    companion object {
        fun matchImage(categoryName: String):Int {
            var imgId: Int = R.drawable.ic_exposure_zero_black_24dp
            when(categoryName) {
                "식비" -> imgId = R.drawable.ic_restaurant
                "카페/간식" -> imgId = R.drawable.ic_coffee
                "술/유흥" -> imgId = R.drawable.ic_drink
                "생활" -> imgId = R.drawable.ic_couch
                "쇼핑" -> imgId = R.drawable.ic_shopping
                "뷰티/미용" -> imgId =  R.drawable.ic_cosmetics
                "교통" -> imgId = R.drawable.ic_bus
                "자동차" -> imgId = R.drawable.ic_car
                "주거/통신" -> imgId = R.drawable.ic_phone
                "의료/건강" -> imgId = R.drawable.ic_health
                "금융" -> imgId = R.drawable.ic_finance
                "문화/여가" -> imgId = R.drawable.ic_hobby
                "여행/숙박" -> imgId = R.drawable.ic_trip
                "교육/학습" -> imgId = R.drawable.ic_education
                "자녀/육아" -> imgId = R.drawable.ic_baby
                "경조/선물" -> imgId = R.drawable.ic_gift
            }
            return imgId
        }
    }
}