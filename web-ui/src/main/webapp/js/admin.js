$(document).ready(function(){
    $("#identifier").mask("99-9999-9999-9999-999X",{translation: {'X': {pattern: /[0-9xX]/, optional: true}}});
    $("#cellPhone").mask("999-9999-9999");
});
