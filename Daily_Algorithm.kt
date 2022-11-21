val now_year = 2022
val now_month = 11
val now_date = 21

val sample_deadLine_month : Array<Int> = arrayOf(12, 11, 11, 1)
val sample_deadLine_date : Array<Int> = arrayOf(1, 22, 23, 1)
val sample_timeH : Array<String> = arrayOf("02", "01", "03", "01")
val sample_timeM : Array<String> = arrayOf("50", "30", "10", "00")
var sample_work : ArrayList<Int> = arrayListOf<Int>(0, 1, 2, 3) // 실제 정렬해서 출력할 일정.
var sample_important : ArrayList<Boolean> = arrayListOf<Boolean>(false, false, true, false)
var sample_num = 4

//가용시간 샘플
var sample_time2 = 400

//가중치 : (데드라인 - 현재날짜) * 1000 + 소요시간
fun find_weight(i : Int) : Int{
    var sample_weight = 0
    sample_weight += if ((sample_deadLine_month[i] - now_month)>=0) (sample_deadLine_month[i] - now_month) else 12 - (sample_deadLine_month[i] - now_month)
    sample_weight += if ((sample_deadLine_date[i] - now_date)>=0) (sample_deadLine_date[i] - now_date) else 31 - (sample_deadLine_date[i] - now_date)
    sample_weight *= 1000
    sample_weight += sample_timeH[i].toInt() * 60 + sample_timeM[i].toInt()
    return sample_weight
}

//sample_work, sample_important 를 가중치에 작은 순으로 정렬
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

    }
}

//HH시간 MM분 -> X분 으로 변환
fun time_to_minute(i : Int) : Int{
    return sample_timeH[i].toInt() * 60 + sample_timeM[i].toInt()
}

//일정 총 소요시간
fun find_daily_all() : Int{
    var daily_all = 0
    for(i in 0 until sample_num) daily_all += sample_timeH[i].toInt() * 60 + sample_timeM[i].toInt()
    return daily_all
}

//중요일정 먼저 출력
fun print_important(){

    var i = 0

    while(i < sample_num && sample_time2 > 0){

        var work = time_to_minute(i)

        if(sample_important[i]==true){
            println(sample_work[i])
            sample_work.removeAt(i)
            sample_important.removeAt(i)
            sample_time2 -= work
            sample_num -= 1
        }
        i += 1
    }
}

//important 한 일정 먼저 출력하고 가중치순으로 출력.
//총 가용시간 >= 일정 총 소요시간 : 모두 출력
//총 가용시간 < 일정 총 소요시간 : 가능한 일정까지 출력 , 가용시간이 0이 되도록 일정을 쪼개서 출력
fun print_daily_work(){

    sort_daily()

    var daily_all = find_daily_all()

    if(daily_all<=sample_time2){

        print_important()

        for(work in sample_work){
           println(work)
        }

    }
    else{

        print_important()

        for(i in 0 until sample_num){
            if(sample_time2 - time_to_minute(i) >= 0) {
                println(sample_work[i])
                sample_time2 -= time_to_minute(i)
            }
            else {
                println("${sample_work[i]} : ${sample_time2}")
                break
            }
        }

    }


}

fun main(){
    print_daily_work()
}
