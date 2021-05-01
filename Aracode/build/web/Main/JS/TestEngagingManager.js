/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
$(document).ready(function () {
    $('#assign-test').on('click', '.test-expand', function () {
        $(this).prev().collapse('toggle');
        $(this).next().collapse('toggle');
    });

    $('#assign-test, #run-test').on('click', '.test-expand2', function () {
        $(this).prev().collapse('toggle');
        $(this).parent().next().collapse('toggle');
    });

    $('#assign-test, #run-test').on('click', '.class-chooser label input', function () {
        var inputs = $(this).parent().next().find('input');
        for (var i = 0; i < inputs.length; i++) {
            $(inputs[i]).prop('checked', this.checked);
        }
    });
});