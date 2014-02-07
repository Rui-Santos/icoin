$(document).ready(function () {
    $("#signup input").tooltip({
        container: "body"
    });
    $("#signin input").tooltip({
        container: "body"
    });

    $("[data-toggle='switch']").wrap('<div class="switch" />').parent().bootstrapSwitch();
});

// NOTICE!! DO NOT USE ANY OF THIS JAVASCRIPT
// IT'S ALL JUST JUNK FOR OUR DOCS!
// ++++++++++++++++++++++++++++++++++++++++++

/*!
 * Copyright 2013 Twitter, Inc.
 *
 * Licensed under the Creative Commons Attribution 3.0 Unported License. For
 * details, see http://creativecommons.org/licenses/by/3.0/.
 */


!function ($) {

    $(function(){

        // IE10 viewport hack for Surface/desktop Windows 8 bug
        //
        // See Getting Started docs for more information
        if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
            var msViewportStyle = document.createElement("style");
            msViewportStyle.appendChild(
                document.createTextNode(
                    "@-ms-viewport{width:auto!important}"
                )
            );
            document.getElementsByTagName("head")[0].
                appendChild(msViewportStyle);
        }


//        var $window = $(window)
        var $body = $(document.body)

        var navHeight = $('.navbar').outerHeight(true) + 10

        $body.scrollspy({
            target: '.bs-sidebar',
            offset: navHeight
        })

//            $window.on('load', function () {
//                $body.scrollspy('refresh')
//            })

        // back to top
        setTimeout(function () {
            var $sideBar = $('.bs-sidebar')
            $sideBar.affix({
                offset: {
                    top: function () {
                        var offsetTop      = $sideBar.offset().top
                        var sideBarMargin  = parseInt($sideBar.children(0).css('margin-top'), 10)
                        var navOuterHeight = $('#header').height()

                        return (this.top = offsetTop - navOuterHeight - sideBarMargin)
                    }
                    , bottom: function () {
                        return (this.bottom = $('footer').outerHeight(true))
                    }
                }
            })
        }, 10)
    })

}(jQuery)