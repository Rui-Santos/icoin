function ajax_form(formId) {
    $('#' + formId).ajaxForm({
        target: '#main'//结果目标页面插入的id为#main的element下
    });
}


function usd_calc(a, b, c) {
    var d = /^[0-9.]{1,}$/i;
    d.test($("#" + a).val()) ? d.test($("#" + b).val()) ? (a = $("#" + a).val() * $("#" + b).val(), $("#" + c).html(a.toFixed(2))) : $("#" + c).html("0") : $("#" + c).html("0")
}
function ex_trade(a, b) {
    "buy" == a && (tr = "b");
    "sell" == a && (tr = "s");
    var c = $("#token").val(), d = $("#" + tr + "_btc").val(), e = $("#" + tr + "_price").val();
    showLoader();
    $.post(_url("order"), {trade: a, btc_count: d, btc_price: e, pair: b, token: c}, function (a) {
        "n" == a.error ? nPopReady(510, 240) : nPopReady(430, 70);
        $("#nPopupCon").html(a.data);
        hideLoader()
    }, "json")
}
function ex_calculate(a, b) {
    "buy" == a && (tr = "b");
    "sell" == a && (tr = "s");
    var c = $("#" + tr + "_btc").val(), d = $("#" + tr + "_price").val();
    $("#" + tr + "_loading").html(loadingnorm);
    $.post(_url("order"), {calculate: a, btc_count: c, btc_price: d, pair: b}, function (a) {
        $("#" + tr + "_all").html(a.all);
        $("#" + tr + "_fee").html(a.fee);
        $("#" + tr + "_comm").html(a.comm);
        "y" == a.error ? $("#" + tr + "_error").show() : $("#" + tr + "_error").hide();
        $("#" + tr + "_loading").html("")
    }, "json")
}


function loadTimeout(){
    var
    //超时秒数
        second = 10
    //计时器
    timer = setInterval(function(){
        if(--second < 1){
            document.getElementById('loading').innerHTML = '您当前的网络连接过慢！';
            clearInterval(timer)
        }
    },1000);
    //注册事件
    document.attachEvent ? document.attachEvent('onreadystatechange',CtrlLoad) : document.onreadystatechange = CtrlLoad;
    //控制加载
    function CtrlLoad(){
        if(document.readyState && ('complete' == document.readyState)){
            document.getElementById('loading').style.display = 'none';
            clearInterval(timer)
        }
    }
}
//调用
loadTimeout()