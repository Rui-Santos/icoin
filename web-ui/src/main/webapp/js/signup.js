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
        messages: {
            email: {
                required: "Please Enter an Email Address"
            },
            password: {
                required: "Please Enter a Password"
            },
            confirmedPassword: {
                required: "Please Confirm Your Password",
                equalTo: "Password must equal to confirmed password"
            },
            aggreed: {
                required: "Please Accept the terms"
            }, aggreed: {
                required: "Please Accept the terms"
            }, aggreed: {
                required: "Please Accept the terms"
            },
            identifier: {
                required: "Please Enter The Captcha Code",
                length: "Captcha Entered Incorrectly"
            }

        },
        highlight: function (element) {
//            console.log($(element).prop("tagName"));
//            console.log($(element).attr("type"));
//            console.log($(element).closest('.form-group').prop("tagName"));
//            console.log($("#password").prop("tagName"));
//            console.log($(element).parent().prop("tagName"));
            $(element).parent().removeClass('has-success');
            $(element).parent().addClass('has-error');
        },
        unhighlight: function (element) {
//            console.log("unhiglight: " + $(element).parent().prop("tagName"));
            $(element).parent().removeClass('has-error');
            $(element).parent().addClass('has-success');

        }, submitHandler: function (form) {
            form.submit();
        }
    });

//    $.validator.addMethod("password", function (value, element) {
//        return this.optional(element) || /^[A-Za-z0-9!@#$%^&*()_]{6,16}$/i.test(value);
//    }, "Passwords are 6-16 characters");
//
//    $.validator.addMethod("identifier", function(value, element)
//    {
//        /^[0-9\s.\-_']{18}$/i.test(value);
//    }, "Please Only Enter Alpha Numeric Characters and Spaces");

});

//$(document).ready(function () {
//    $("#signup input").tooltip();
//    $("#changePassword input").tooltip();
//    $("#changeWithdrawPassword input").tooltip();
//});

$(document).ready(function () {
    $("#captchaImage").click(function () {
        alert("dfds");
        $("#captchaImage").attr("src", "http://localhost:8080/simpleCaptcha.png");
    });
});