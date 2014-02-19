$(document).ready(function () {
    $("#sellOrder").validate({
        rules: {
            tradeAmount: {
                required: true,
                number:true,
                min:10
            },
            itemPrice: {
                required: true,
                number:true,
                min:0
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
            console.log("success:"+$(element).parent().attr("tagName"));
            console.log("success:"+$(element).parent().attr("class"));
            $(element).parent().removeClass('has-error');
            $(element).parent().addClass('has-success');

        },
        submitHandler: function (form) {
            form.submit();
        },
        showErrors: function(errorMap, errorList) {

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
                number:true,
                min:10
            },
            itemPrice: {
                required: true,
                number:true,
                min:0
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
        showErrors: function(errorMap, errorList) {

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
});