(function($) {
    $.extend($.validator.messages, {
        required: "必须填写",
        remote: "请输入有效字段",
        email: "请输入有效电子邮件",
        url: "请输入有效网址",
        date: "请输入有效日期",
        dateISO: "请输入有效日期 (YYYY-MM-DD)",
        number: "请输入合法数字",
        digits: "请输入合法整数",
        creditcard: "请输入有效的信用卡号码",
        equalTo: "请再次输入相同的值",
        extension: "请输入有效的后缀",
        maxlength: $.validator.format("最多 {0} 个字"),
        minlength: $.validator.format("最少 {0} 个字"),
        rangelength: $.validator.format("请输入长度为 {0} 至 {1} 之間的字串"),
        range: $.validator.format("请输入 {0} 至 {1} 之间的数值"),
        max: $.validator.format("请输入不大于 {0} 的数值"),
        min: $.validator.format("请输入不小于 {0} 的数值")
    });
}(jQuery));