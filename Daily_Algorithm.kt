val now_year = 2022
val now_month = 11
val now_date = 21

val fixed_start_H : Array<String> = arrayOf("09", "12", "16", "22")
val fixed_start_M : Array<String> = arrayOf("00", "10", "30", "00")
val fixed_end_H : Array<String> = arrayOf("09", "13", "18")
val fixed_end_M : Array<String> = arrayOf("50", "00", "30")

//진행할 고정일정
var now_fixed = 0
//고정일정 개수
var fixed_num = 3

val sleep_start_H = 22
val sleep_start_M = 0
val sleep_end_H = 8
val sleep_end_M = 0

val sample_deadLine_month : Array<Int> = arrayOf(12, 11, 11, 1)
val sample_deadLine_date : Array<Int> = arrayOf(1, 22, 23, 1)
val sample_timeH : ArrayList<String> = arrayListOf("02", "01", "03", "01")
val sample_timeM : ArrayList<String> = arrayListOf("50", "30", "10", "00")
var sample_work : ArrayList<Int> = arrayListOf<Int>(0, 1, 2, 3) // 실제 정렬해서 출력할 일정.
var sample_important : ArrayList<Boolean> = arrayListOf<Boolean>(false, false, true, false)
var sample_num = 4

//현재 시간
var now_time_H = sleep_end_H
var now_time_M = sleep_end_M

//출력할 결과를 저장하는 배열
var result_start_H = arrayListOf<String>()
var result_start_M = arrayListOf<String>()
var result_end_H =  arrayListOf<String>()
var result_end_M =  arrayListOf<String>()
var result_work = arrayListOf<Int>()

//가중치 : (데드라인 - 현재날짜) * 1000 + 소요시간
fun find_weight(i : Int) : Int{
    var sample_weight = 0
    sample_weight += if ((sample_deadLine_month[i] - now_month)>=0) (sample_deadLine_month[i] - now_month) else 12 - (sample_deadLine_month[i] - now_month)
    sample_weight += if ((sample_deadLine_date[i] - now_date)>=0) (sample_deadLine_date[i] - now_date) else 31 - (sample_deadLine_date[i] - now_date)
    sample_weight *= 1000
    sample_weight += sample_timeH[i].toInt() * 60 + sample_timeM[i].toInt()
    return sample_weight
}

//sample_work, sample_important, sample_timeH, sample_timeM 를 가중치에 작은 순으로 정렬
fun sort_daily(){
    for(i in 0 until sample_num){
        var min_weight = find_weight(i)
        var min_index = i

        for(j in i until sample_num){
            var temp_weight = find_weight(j)
            if(min_weight > temp_weight) {
                min_weight = temp_weight
                min_index = j
            }
        }


        var temp_work = sample_work[min_index]
        sample_work[min_index] = sample_work[i]
        sample_work[i] = temp_work

        var temp_important = sample_important[min_index]
        sample_important[min_index] = sample_important[i]
        sample_important[i] = temp_important

        var temp_timeH = sample_timeH[min_index]
        sample_timeH[min_index] = sample_timeH[i]
        sample_timeH[i] = temp_timeH

        var temp_timeM = sample_timeM[min_index]
        sample_timeM[min_index] = sample_timeM[i]
        sample_timeM[i] = temp_timeM
    }
}

//HH시간 MM분 -> X분 으로 변환
fun time_to_minute(i : Int) : Int{
    return sample_timeH[i].toInt() * 60 + sample_timeM[i].toInt()
}

//Daily일정을 진행함
fun doDaily(hour : Int, minute : Int){

    if(now_time_M + minute >= 60){
        now_time_H = now_time_H + hour + 1
        now_time_M = now_time_M + minute - 60
    }
    else{
        now_time_H = now_time_H + hour
        now_time_M = now_time_M + minute
    }

}

//Daily일정을 진행할 때 fixed일정과 겹치는지 체크
fun isConflict() : Boolean{

    if(now_time_H > fixed_start_H[now_fixed].toInt() || (now_time_H == fixed_start_H[now_fixed].toInt() && now_time_M > fixed_start_M[now_fixed].toInt())) {
        return true
    }
    return false
}

//고정 일정과 충돌이 있을경우 now_time을 업데이트함
fun updateNowTime(i : Int){

    if(now_time_M - fixed_start_M[i].toInt() + fixed_end_M[i].toInt() >= 60){
        now_time_H = now_time_H - fixed_start_H[i].toInt() + fixed_end_H[i].toInt() + 1
        now_time_M = now_time_M - fixed_start_M[i].toInt() + fixed_end_M[i].toInt() - 60
    }
    else{
        now_time_H = now_time_H - fixed_start_H[i].toInt() + fixed_end_H[i].toInt()
        now_time_M = now_time_M - fixed_start_M[i].toInt() + fixed_end_M[i].toInt()
    }

}

//일정 진행시간이 수면시간을 넘어가는지 판단. 넘어가면 true
fun isSleep() : Boolean{
    if(now_time_H > sleep_start_H || (now_time_H == sleep_start_H && now_time_M > sleep_start_M)){
        return true
    }
    return false
}

//중요일정 먼저 result배열에 저장
//now_time이 sleep_start를 넘어가는지를 통해 하루 안에 일정들을 다 소화할 수 있는지 판단.
fun put_important_result(){

    var i = 0

    while(i < sample_num && !isSleep()){

        var work = time_to_minute(i)
        var work_H = work / 60
        var work_M = work % 60
        var now_fixed_num = now_fixed

        if(result_start_H.size > result_end_H.size){
            result_end_H.add(now_time_H.toString())
            result_end_M.add(now_time_M.toString())
        }

        if(sample_important[i]==true){

            result_start_H.add(now_time_H.toString())
            result_start_M.add(now_time_M.toString())
            result_work.add(sample_work[i])

            doDaily(work_H, work_M)

            if(isConflict()){

                //하나 이상의 고정일정에 대해 겹치는지 확인
                for(k in now_fixed_num until fixed_num){

                    if(fixed_start_H[k].toInt() < now_time_H || (fixed_start_H[k].toInt() == now_time_H && fixed_start_M[k].toInt() < now_time_M)){

                        if(isSleep()){
                            result_end_H.add(sleep_start_H.toString())
                            result_end_M.add(sleep_start_M.toString())
                            break
                        }

                        result_end_H.add(fixed_start_H[k])
                        result_end_M.add(fixed_start_M[k])

                        result_start_H.add(fixed_end_H[k])
                        result_start_M.add(fixed_end_M[k])
                        result_work.add(sample_work[i])

                        updateNowTime(k)
                        now_fixed += 1
                    }
                    else{

                        if(isSleep()){
                            result_end_H.add(sleep_start_H.toString())
                            result_end_M.add(sleep_start_M.toString())
                            break
                        }

                        result_end_H.add(now_time_H.toString())
                        result_end_M.add(now_time_M.toString())
                        break
                    }

                }

            }
            else {

                result_end_H.add(now_time_H.toString())
                result_end_M.add(now_time_M.toString())

            }

            //result에 put한 important 일정들은 삭제함.
            sample_work.removeAt(i)
            sample_important.removeAt(i)
            sample_timeH.removeAt(i)
            sample_timeM.removeAt(i)
            sample_num -= 1
        }
        i += 1
    }
}

//일반 일정들을 result배열에 저장
fun put_result(){

    for(i in 0 until sample_num){

        var work = time_to_minute(i)
        var work_H = work / 60
        var work_M = work % 60
        var now_fixed_num = now_fixed

        if(result_start_H.size > result_end_H.size){
            result_end_H.add(now_time_H.toString())
            result_end_M.add(now_time_M.toString())
        }

        result_start_H.add(now_time_H.toString())
        result_start_M.add(now_time_M.toString())
        result_work.add(sample_work[i])

        doDaily(work_H, work_M)

        if(isConflict()){
            //하나 이상의 고정일정에 대해 겹치는지 확인
            for(k in now_fixed_num until fixed_num){
                if(fixed_start_H[k].toInt() < now_time_H || (fixed_start_H[k].toInt() == now_time_H && fixed_start_M[k].toInt() < now_time_M)){

                    if(isSleep()){
                        result_end_H.add(sleep_start_H.toString())
                        result_end_M.add(sleep_start_M.toString())
                        break
                    }

                    result_end_H.add(fixed_start_H[k])
                    result_end_M.add(fixed_start_M[k])

                    result_start_H.add(fixed_end_H[k])
                    result_start_M.add(fixed_end_M[k])
                    result_work.add(sample_work[i])

                    updateNowTime(k)
                    now_fixed += 1
                }
                else{

                    if(isSleep()){
                        result_end_H.add(sleep_start_H.toString())
                        result_end_M.add(sleep_start_M.toString())
                        break
                    }

                    result_end_H.add(now_time_H.toString())
                    result_end_M.add(now_time_M.toString())
                    break
                }

            }

        }
        else {
            result_end_H.add(now_time_H.toString())
            result_end_M.add(now_time_M.toString())

        }

    }

}

//important 한 일정 먼저 출력하고 가중치순으로 출력.
//총 가용시간 >= 일정 총 소요시간 : 모두 출력
//총 가용시간 < 일정 총 소요시간 : 가능한 일정까지 출력 , 가용시간이 0이 되도록 일정을 쪼개서 출력
fun print_daily_work(){

    sort_daily()

    put_important_result()

    put_result()



    for(i in 0 until result_work.size){

        println(result_work[i].toString() + " " + result_start_H[i] + " : " + result_start_M[i] + " ~ " + result_end_H[i] + " : " + result_end_M[i])

    }

}

fun main(){
    print_daily_work()
}
