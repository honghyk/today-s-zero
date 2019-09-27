package com.example.todayzero.util

import android.app.ProgressDialog
import android.os.AsyncTask
import android.renderscript.ScriptGroup
import android.util.Log
import com.example.todayzero.data.Notice
import com.example.todayzero.data.Store
import com.example.todayzero.data.source.DataFilterType
import com.example.todayzero.notice.NoticeRepository
import com.example.todayzero.data.source.DataSource
import com.example.todayzero.db.DBHelper
import com.example.todayzero.findstore.StoreRepository
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.InputStream

class NetworkTask(
    val dataType: DataFilterType,
    var url: String,
    val callback: DataSource.ApiListener
) : AsyncTask<String, Void, ArrayList<String>>() {
    lateinit var storeList: ArrayList<ArrayList<Store>>
    lateinit var noticeList: ArrayList<Notice>
    var limitNum: Int? = null
    lateinit var fisArr: Array<InputStream>
    lateinit var dbHelper: DBHelper
    var strArr=ArrayList<String>()
    val zone= mutableMapOf<String,Int>("강남구" to 0,"강동구"  to 1, "강북구"  to 2, "강서구"  to 3,"관악구"  to 4,"광진구"  to 5,
        "구로구"  to 6,"금천구"  to 7, "노원구" to 8, "도봉구" to 9, "동대문구" to 10,  "동작구" to 11,  "마포구" to 12, "서대문구" to 13, "서초구" to 14,
        "성동구" to 15, "성북구" to 16, "송파구" to 17,  "양천구" to 18, "영등포구" to 19,  "용산구" to 20,"은평구" to 21,"종로구" to 22, "중구" to 23, "중랑구" to 24)



    constructor(dataList: ArrayList<*>, dataType: DataFilterType, url: String, callback: DataSource.ApiListener, limitNum: Int) : this(dataType, url, callback) {
        noticeList = dataList as ArrayList<Notice>
        this.limitNum = limitNum
    }

    constructor(dataList: ArrayList<*>, dataType: DataFilterType, fisArr: Array<InputStream>, callback: DataSource.ApiListener) : this(dataType, "", callback) {
        storeList = dataList as ArrayList<ArrayList<Store>>
        this.fisArr = fisArr
    }
    constructor(dataList: ArrayList<*>, dbHelper:DBHelper,dataType: DataFilterType, callback: DataSource.ApiListener) : this(dataType, "", callback)
    {
        storeList = dataList as ArrayList<ArrayList<Store>>
        this.dbHelper=dbHelper
    }

    override fun doInBackground(vararg params: String?): ArrayList<String> {
        if (dataType == DataFilterType.STORE_RAW) {
            for (fis in fisArr) {
                var sb = StringBuilder()
                var bis = BufferedInputStream(fis)
                var fileBody = ByteArray(fis.available())
                var len = bis.read(fileBody, 0, fileBody.size)
                if (len != -1)
                    sb.append(String(fileBody, charset("utf-8")))
                fis.close()
                bis.close()
                strArr.add(sb.toString())
            }
         //   return sb.toString()
        }
        else if(dataType==DataFilterType.STORE_DB){
            for (i in 1..25) {
                storeList.add(dbHelper.getStores(i))
            }
            strArr.add("")
        }
        else
            strArr.add(RequestHttpURLConnection().request(url))

        return strArr
    }

    override fun onPostExecute(result:ArrayList<String>?) {
        super.onPostExecute(result)
        var res=result!!.first()
        if (res == "ERROR") {
            callback.onFailure(dataType)
        } else {
            when (dataType) {
                DataFilterType.STORE_RAW -> {
                    for(arr in result){
                        val storeArr = JSONArray(arr)
                        for (i in 0 until storeArr.length()) {
                            val storeObj = storeArr.getJSONObject(i)
                            val sName = storeObj.getString("name")
                            val sAddr = storeObj.getString("addr")
                            val sLocality = storeObj.getString("locality")
                            val sType = storeObj.getString("type")
                            var a=zone.keys
                            if(a.contains(sLocality))
                                storeList.get(zone[sLocality]!!).add(Store(-1,sName, sAddr, sLocality, "", sType))
                            else {
                                Log.i("test", sLocality)
                                Log.i("test", sName)
                            }
                            // [] 로 끝나는 파일들이 11개 읽혀서 문제 생김 -> sb 내용을 리스트에 담아서 리스트를 넘겨야 할듯! ! ! ! !
                        }
                    }

                }
//                DataFilterType.STORE -> {
//                    val storeObj = JSONObject(result)
//                    val status = storeObj.optString("result")
//                    if (status == "FAIL") {
//                        callback.onFailure(DataFilterType.STORE)
//                    } else {
//                        val storeArr = storeObj.getJSONArray("list")
//                        for (i in 0 until storeArr.length()) {
//                            val sObj = storeArr.getJSONObject(i)
//                            val name = sObj.optString("pobsAfstrName")
//                            val addr1 = sObj.optString("pobsBaseAddr")
//                            val addr2 = sObj.optString("pobsDtlAddr")
//                            val type = sObj.optString("bztypName")
//                            storeList.get(0).add(Store(-1,name, addr1 + " " + addr2, "", "", type)) //임시로 0
//                            //db에도 insert 하기 ! 데이터가 추가될때 끝에 추가되는게 아닌데, 중간에 출력되는거면 일치여부를 다 검사해봐야할듯 . ..
//
//                        }
//                    }
//                }
//                DataFilterType.STORE_NUM -> {
//                    val storeObj = JSONObject(result)
//                    val status = storeObj.optString("result")
//                    if (status == "FAIL") {
//                        callback.onFailure(DataFilterType.STORE_NUM)
//                    } else {
//                        val storeNum = storeObj.optInt("totalCnt")
//                        (callback as StoreRepository.StoreNumApiListener).onDataLoaded(storeNum)
//                    }
//                }
                DataFilterType.NOTICE -> {

                    var idx1 = res!!.indexOf("<tbody>")
                    var idx2 = res!!.indexOf("</tbody>")

                    var rawText = res.slice(IntRange(idx1, idx2))

                    for (i in 0 until limitNum!!) {
                        var endIdx = rawText.indexOf("a>")
                        if (endIdx == -1)
                            break
                        rawText = rawText.substring(endIdx + 2)
                    }

                    while (true) {
                        var startIdx = rawText.indexOf("<a")
                        if (startIdx == -1)
                            break
                        var endIdx = rawText.indexOf("a>")
                        var title = rawText.slice(IntRange(startIdx, endIdx))
                        rawText = rawText.substring(endIdx + 2)//a>까지 같이 짤리는데 없애려고 +2함
                        //타이틀 제목 가져오기
                        startIdx = title.indexOf(";\">")
                        endIdx = title.indexOf("</")
                        val titleText =
                            title.slice(IntRange(startIdx + 3, endIdx - 1)).replace("&#039;", "'")
                                .replace("&#034;", "\"") //자름문자 빼주기

                        //타이틀 번호 가져오기 (디테일 url로 넘겨짐)
                        startIdx = title.indexOf("goDetail('")
                        endIdx = title.indexOf("');")
                        val titleNum = title.slice(IntRange(startIdx + 10, endIdx - 1)) //자름문자 빼주기

                        startIdx = rawText.indexOf("<td>")
                        rawText = rawText.substring(startIdx + 4)
                        startIdx = rawText.indexOf("</td>")
                        rawText = rawText.substring(startIdx + 4)
                        startIdx = rawText.indexOf("<td>")
                        endIdx = rawText.indexOf("</td>")
                        val date = rawText.slice(IntRange(startIdx + 4, endIdx - 1))
                        noticeList.add(Notice(titleText, "", date, titleNum))

                    }
                    //날짜 파싱해야함!!
                }
                DataFilterType.NOTICE_NUM -> {
                    var idx1 = res!!.indexOf("<tbody>")
                    var idx2 = res!!.indexOf("</tbody>")

                    var rawText = res.slice(IntRange(idx1, idx2 + 8))
                    while (true) {
                        var startIdx = rawText.indexOf("<a")
                        if (startIdx == -1)
                            break
                        var endIdx = rawText.indexOf("a>")
                        var title = rawText.slice(IntRange(startIdx, endIdx))
                        rawText = rawText.substring(endIdx + 2)//a>까지 같이 짤리는데 없애려고 +2함

                        //타이틀 제목 가져오기
                        startIdx = title.indexOf(";\">")
                        endIdx = title.indexOf("</")
                        val titleText =
                            title.slice(IntRange(startIdx + 3, endIdx - 1)).replace("&#039;", "'")
                                .replace("&#034;", "\"") //자름문자 빼주기

                        //타이틀 번호 가져오기 (디테일 url로 넘겨짐)
                        startIdx = title.indexOf("goDetail('")
                        endIdx = title.indexOf("');")
                        val titleNum = title.slice(IntRange(startIdx + 10, endIdx - 1)) //자름문자 빼주기

                        //날짜 가져오기
                        startIdx = rawText.indexOf("<td>")
                        rawText = rawText.substring(startIdx + 4)
                        startIdx = rawText.indexOf("</td>")
                        rawText = rawText.substring(startIdx + 4)
                        startIdx = rawText.indexOf("<td>")
                        endIdx = rawText.indexOf("</td>")
                        val date = rawText.slice(IntRange(startIdx + 4, endIdx - 1))
                        noticeList.add(Notice(titleText, "", date, titleNum))
                    }
                    (callback as NoticeRepository.NoticeNumApiListener).onDataLoaded(noticeList.size)
                }
                DataFilterType.NOTICE_DETAIL -> {
                    var idx1 = res!!.indexOf("<tbody>")
                    var idx2 = res!!.indexOf("</tbody>")

                    var detailText = res!!.slice(IntRange(idx1, idx2))

                    idx1 = detailText.indexOf("<td>")
                    idx2 = detailText.indexOf("</td>")
                    detailText = detailText.slice(IntRange(idx1 + 4, idx2 - 1))

                    detailText = detailText.replace("&nbsp;", " ")
                    detailText = detailText.replace("▶", "▶\n")
                    detailText = detailText.replace("<br/>", "\n")
                    detailText = detailText.replace("&lt;", "<")
                    detailText = detailText.replace("&gt;", ">")
                    detailText = detailText.replace(
                        "                                                                                ",
                        "\n"
                    )
                    detailText = detailText.replace(
                        "                                                                                ",
                        "\n"
                    )
                    noticeList[limitNum!!].content = detailText

                }

            }
            callback.onDataLoaded(dataType)
        }

    }


}