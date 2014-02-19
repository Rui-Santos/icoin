$(document).ready(function () {

    $("#signin").validate({
        rules: {
            j_username: {
                required: true,
                rangelength:[6,16]
            },
            j_password: {
                required: true,
                rangelength:[6,16]
            }
        },
        highlight: function (element) {
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

//                console.log(errorList);
//                console.log(errorMap);

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
