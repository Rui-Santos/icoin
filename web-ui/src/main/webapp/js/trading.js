$(document).ready(function () {
    $("#sellOrder").validate({
        rules: {
            tradeAmount: {
                required: true,
                min: 0.001,
                max: 999999999
            },
            itemPrice: {
                required: true,
                min: 0.001,
                max: 999999999
            }

        },
//        messages: { 
//        }, 
        highlight: function (element) {
            console.log($(element).parent().attr("tagName"));
            console.log($(element).parent().attr("class"));
            $(element).parent().removeClass('has-success');
            $(element).parent().addClass('has-error');
        },
        unhighlight: function (element) {
            console.log("success:" + $(element).parent().attr("tagName"));
            console.log("success:" + $(element).parent().attr("class"));
            $(element).parent().removeClass('has-error');
            $(element).parent().addClass('has-success');

        },
        submitHandler: function (form) {
            form.submit();
        },
        showErrors: function (errorMap, errorList) {

            // Clean up any tooltips for valid elements 
            $.each(this.validElements(), function (index, element) {
                var $element = $(element);

                $element.data("title", "") // Clear the title - there is no error associated anymore 
//                    .removeClass("has-error") 
                    .tooltip("destroy");
                $(element).parent().removeClass('has-error');
                $(element).parent().addClass('has-success');
            });

            // Create new tooltips for invalid elements 
            $.each(errorList, function (index, error) {
                var $element = $(error.element);

                ($element).parent().removeClass('has-success');
                ($element).parent().addClass('has-error');

                $element.tooltip("destroy") // Destroy any pre-existing tooltip so we can repopulate with new tooltip content 
                    .data("title", error.message)
//                    .addClass("has-error") 
                    .tooltip(); // Create a new tooltip based on the error messsage we just set in the title 

            });
        }
    });


    $("#buyOrder").validate({
        rules: {
            tradeAmount: {
                required: true,
                min: 0.001,
                max: 999999999
//                number:true, 

            },
            itemPrice: {
                required: true,
                min: 0.001,
                max: 999999999999
//                number:true, 
            }

        },
//        messages: { 
//        }, 
        highlight: function (element) {
            $(element).parent().removeClass('has-success');
            $(element).parent().addClass('has-error');
        },
        unhighlight: function (element) {
            $(element).parent().removeClass('has-error');
            $(element).parent().addClass('has-success');

        }, submitHandler: function (form) {
            form.submit();
        },
        showErrors: function (errorMap, errorList) {

            // Clean up any tooltips for valid elements 
            $.each(this.validElements(), function (index, element) {
                var $element = $(element);

                $element.data("title", "") // Clear the title - there is no error associated anymore 
//                    .removeClass("has-error") 
                    .tooltip("destroy");
                $(element).parent().removeClass('has-error');
                $(element).parent().addClass('has-success');
            });

            // Create new tooltips for invalid elements 
            $.each(errorList, function (index, error) {
                var $element = $(error.element);

                ($element).parent().removeClass('has-success');
                ($element).parent().addClass('has-error');

                $element.tooltip("destroy") // Destroy any pre-existing tooltip so we can repopulate with new tooltip content 
                    .data("title", error.message)
//                    .addClass("has-error") 
                    .tooltip(); // Create a new tooltip based on the error messsage we just set in the title 

            });
        }
    });

    $.validator.addMethod("buyGreaterThan",

        function (value, element) {
            var balance = $("#balanceToBuy").text().trim();
            var priceToBuy = $("#priceToBuy").val();
            var amountToBuy = $("#amountToBuy").val();
//            console.log("balance:" + balance); 
//            console.log("priceToBuy:" + priceToBuy); 
//            console.log("amountToBuy:" + amountToBuy); 
//            console.log("isNaN(amountToBuy):" + isNaN(amountToBuy)); 
            if (!$.isNumeric(amountToBuy) || !$.isNumeric(priceToBuy)) {
                return true;
            }
            var balanceMoney = parseFloat(balance);
            var price = parseFloat(priceToBuy);
            var amount = parseFloat(amountToBuy);

//            console.log("balanceMoney:" + balanceMoney); 
//            console.log("price * amount:" + price * amount); 
//            console.log("balanceMoney > (price * amount):" + (balanceMoney > (price * amount))); 
            return balanceMoney >= (price * amount * 1.005);
        });

    $.validator.addMethod("sellGreaterThan",

        function (value, element) {
            var balance = $("#balanceToSell").text().trim();
            var amountToSell = $("#amountToSell").val();
            if (!$.isNumeric(amountToSell)) {
                return true;
            }
            var balanceMoney = parseFloat(balance);
            var amount = parseFloat(amountToSell);

            return balanceMoney >= (amount * 1.005);
        });

    $.validator.addClassRules({
        buySubmit: {
            buyGreaterThan: true
        },
        sellSubmit: {
            sellGreaterThan: true
        }
    });

    $("#priceToBuy").keyup(function () {
        var fee = 0.005;
        var priceToBuy = $("#priceToBuy").val();
        var amountToBuy = $("#amountToBuy").val();
        if ($.isNumeric(amountToBuy) && $.isNumeric(priceToBuy)) {
            var price = parseFloat(priceToBuy);
            var amount = parseFloat(amountToBuy);

            var total = price * amount;
            var commission = total * fee;
            $('#totalToBuy').text(total + commission);
            $('#feeToBuy').text(commission);
        } else {
            $('#totalToBuy').text(0);
            $('#feeToBuy').text(0);
        }
        $('#totalToBuy').number(true, 3);
        $('#feeToBuy').number(true, 3);
    });

    $("#amountToBuy").keyup(function () {
        var fee = 0.005;
        var priceToBuy = $("#priceToBuy").val();
        var amountToBuy = $("#amountToBuy").val();
        if ($.isNumeric(amountToBuy) && $.isNumeric(priceToBuy)) {
            var price = parseFloat(priceToBuy);
            var amount = parseFloat(amountToBuy);
            var total = price * amount;
            var commission = total * fee;
            $('#totalToBuy').text(total + commission);
            $('#feeToBuy').text(commission);
        } else {
            $('#totalToBuy').text(0);
            $('#feeToBuy').text(0);
        }
        $('#totalToBuy').number(true, 3);
        $('#feeToBuy').number(true, 3);
    });

    $('#amountToBuy').number(true, 6);
    $('#priceToBuy').number(true, 3);
    $('#totalToBuy').number(true, 3);
    $('#feeToBuy').number(true, 3);

    $('#amountToSell').number(true, 6);
    $('#priceToSell').number(true, 3);
    $('#totalToSell').number(true, 6);
    $('#feeToSell').number(true, 6);
});


$("#priceToSell").keyup(function () {
    var amountToSell = $("#amountToSell").val();
    if ($.isNumeric(amountToSell)) {
        var amount = parseFloat(amountToSell);
        $('#totalToSell').text(amount * 1.005);
        $('#feeToSell').text(amount * 0.005);
    } else {
        $('#totalToSell').text(0);
        $('#feeToSell').text(0);
    }
    $('#totalToSell').number(true, 6);
    $('#feeToSell').number(true, 6);
});

$("#amountToSell").keyup(function () {
    var amountToSell = $("#amountToSell").val();
    if ($.isNumeric(amountToSell)) {
        var amount = parseFloat(amountToSell);
        $('#totalToSell').text(amount * 1.005);
        $('#feeToSell').text(amount * 0.005);
    } else {
        $('#totalToSell').text(0);
        $('#feeToSell').text(0);
    }
    $('#totalToSell').number(true, 6);
    $('#feeToSell').number(true, 6);
}); 