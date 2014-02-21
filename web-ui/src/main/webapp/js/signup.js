$(document).ready(function () {

    $("#signup").validate({
        rules: {
            firstname: "required",
            lastname: "required",
            username: {
                required: true,
                rangelength:[6,16]
//                rangelength:[6,16]
            },
            identifier: {
                required: true,
                rangelength:[18,18]
            },
            password: {
                required: true,
//                minlength: 6,
                rangelength:[6,16]
//                password: true
            },captcha: {
                required: true,
                rangelength:[1,16],
                remote: "validateCaptcha"
            },
            confirmedPassword: {
                required: true,
                rangelength:[6,16],
                equalTo: "#password"
            },
            aggreed: "required",
            email:{
                required:true,
                email:true
//                remote:"check.php"
            }


        },
        highlight: function (element) {
            $(element).parent().removeClass('has-success');
            $(element).parent().addClass('has-error');
        },
        unhighlight: function (element) {
            $(element).parent().removeClass('has-error');
            $(element).parent().addClass('has-success');

        }, submitHandler: function (form) {
            form.submit();
        }
    });
});

//$(document).ready(function () {
//    $("#signup input").tooltip();
//    $("#changePassword input").tooltip();
//    $("#changeWithdrawPassword input").tooltip();
//});

$(document).ready(function () {
    $("#captchaImage").click(function () {
        $("#captchaImage").attr("src", "simpleCaptcha.png");
    });
    $("#cellPhone").mask("999-9999-9999");
    $("#identifier").mask("99-9999-9999-9999-9999");
});