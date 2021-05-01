$(function () {
    $('[data-toggle="tooltip"]').tooltip();
});

$(document).ready(function () {

    var deadline = $('#deadline').val();

    var x = setInterval(function () {
        var now = new Date().getTime();
        var timeToComplete = (deadline - now) / 1000;
        if (timeToComplete >= 0) {
            var days = Math.floor(timeToComplete / 86400);
            var hours = Math.floor(timeToComplete % 86400 / 3600);
            var minutes = Math.floor(timeToComplete % 3600 / 60);
            var seconds = Math.floor(timeToComplete % 60);
            $("#clock").html(days + "d " + hours + "h " + minutes + "m " + seconds + "s ");
        } else {
//            $('input[name="set-unalterable"]').click();
        }
    }, 1000);

    $('input[type="radio"][value="true"], input[type="radio"][value="false"]').click(function () {
        mark($(this), done);
    });

    $('input[type="radio"][value="dont-know"]').click(function () {
        mark($(this), undone);
    });

    $('select[class!="fillInTextQuestion"]').change(function () {
        parent = $(this).closest(".question-UI");
        elements = $(parent).find('select');
        numberOfSelected = 0;
        set = new Set();
        $(elements).each(function () {
            set.add($(this).val());
            if ($(this).val() != "dont-know") {
                numberOfSelected++;
            }
        });
        if (numberOfSelected + 1 === set.size) {
            mark($(this), undone);
        } else if (numberOfSelected == elements.length && set.size == elements.length) {
            mark($(this), done);
        } else {
            mark($(this), error);
        }
    });

    $('select[class="fillInTextQuestion"]').change(function () {
        elements = $(this).closest(".question-UI").find('select');
        var isDone = true;
        elements.each(function () {
            if ($(this).val() == "dont-know") {
                mark($(this), undone);
                isDone = false;
                return;
            }
        });
        if (isDone) {
            mark($(this), done);
        }
    });

    $(window).resize(function () {
        $('body').css("padding-top", $('nav').css('height'));
    });

    var done = "green";
    var undone = "white";
    var error = "red";


    function mark(element, color) {
        $('a[href="#' + $(element).closest(".question-UI").attr("id") + '"]').css("color", color);
    }
});