
var selectedTestAction;
var selectedOption;

function changeShownTestAction(element) {
    selectedTestAction.hide();
    showTestAction(element);
}

function showTestAction(element) {
    selectedTestAction = element;
    selectedTestAction.show();
}

function newTest() {
    changeShownTestAction($("#new-test"));
    return false;
}

function assignTest() {
    changeShownTestAction($("#assign-test"));
    return false;
}

function runTest() {
    changeShownTestAction($("#run-test"));
    return false;
}

function alterTest() {
    changeShownTestAction($("#alter-test"));
    return false;
}

var question =
        '<div class="question p-1 rounded col mx-0 mb-2"> <!--question-->' +
        '             <div class="col-12 row mx-0 px-0">' +
        '                 <div class="question-text-input col form-control container-fluid" placeholder="Otázka" contenteditable="true" maxlength="1500"></div>' +
        '                 <textarea name="question-text" class="question-text" ></textarea>' +
        '                 <div class="question-menu col mx-0 px-0">' +
        '                     <input class="form-control col-12 pr-0" placeholder="Správně" type="number" name="right-option-points" min="1" max="255" required>' +
        '                     <input class="form-control col-12 pr-0" placeholder="Špatně" type="number" name="wrong-option-points" min="0" max="255" required>' +
        '                     <select name="question-type-selector" class="col-4 col-sm-12 custom-select custom-select-sm question-type-selector">' +
        '                         <option value="booleanQuestion" selected>Pravda, nebo lež</option>' +
        '                         <option value="fillInTextQuestion">Doplňte do textu</option>' +
        '                         <option value="connectQuestion">Přiřaďte definici k pojmu</option>' +
        '                         <option value="orderQuestion">Seřaďte pojmy</option>' +
        '                     </select>' +
        '                     <div class="p-1 col-12 mx-0 px-0">' +
        '                         <a class="px-0 mr-1 question-deleter" href="#" data-toggle="tooltip" title="Vymaž otázku">' +
        '                             <img src="images/garbage.png" class="test-icon index-icon">' +
        '                         </a>' +
        '                         <a class="ml-auto px-0" href="#" data-toggle="tooltip" title="Nová otázka">' +
        '                             <img src="images/plus.png" class="question-creator test-icon index-icon">' +
        '                         </a>' +
        '                     </div>' +
        '                     <div class="col question-menu mx-0 p-1">' +
        '                         <a class="up ml-auto px-0" href="#" title="posunout otázku nahoru">' +
        '                             <img src="images/chevron-up.png" class="small-chevron-icon index-icon">' +
        '                         </a>' +
        '                         <a class="down ml-auto px-0" href="#" title="posunout otázku dolů">' +
        '                             <img src="images/chevron-down.png" class="small-chevron-icon index-icon">' +
        '                         </a>' +
        '                         <a class="option-creator ml-auto px-0" href="#" data-toggle="tooltip" title="Nová možnost">' +
        '                             <img src="images/plus.png" class="small-chevron-icon index-icon">' +
        '                         </a>' +
        '                     </div>' +
        '                 </div>' +
        '             </div>' +
        '             <div class="answer-container">' +
        '             </div>' +
        '         </div>';
var prefix = 'radio';
var booleanQuestion =
        '<div class="boolean-question">' +
        '   <input type="radio" name="' + prefix + '0" class="boolean-input" value="true">' +
        '   <label name="' + prefix + '0" class="m-1">Ano</label> ' +
        '   <input type="radio" name="' + prefix + '0" class="boolean-input" value="false">' +
        '   <label name="' + prefix + '0" class="m-1">Ne</label>' +
        '</div>'; //nevim is missing, because it should never be valid option
var wrongOption =
        '<div class="row col-12 col-sm container-fluid option-min-width alert-danger p-0 mx-0">' +
        '    <textarea class="form-control col container-fluid alert-danger" rows="1" name="wrong-option" placeholder="Špatná možnost" required maxlength="60"></textarea>' +
        '    <a class="option-deleter col remove-fill-option mx-0 p-1" href="#" target="_blank" data-toggle="tooltip" title="Odebrat špatnou možnost">' +
        '       <img src="images/garbage.png" class="test-icon index-icon">' +
        '    </a>' +
        '</div>';
var fillInTextQuestion =
        '<div class="fill-in-text-question row mx-0 mt-2 px-0">' +
        '            <div class="col question-menu mx-0 p-1">' +
        '                <a class="option-up ml-auto px-0" href="#" target="_blank">' +
        '                    <img src="images/chevron-up.png" class="chevron-icon index-icon" title="posunout pod-otázku nahoru">' +
        '                </a>' +
        '                <a class="option-down ml-auto px-0" href="#" target="_blank">' +
        '                    <img src="images/chevron-down.png" class="chevron-icon index-icon" title="posunout pod-otázku dolů">' +
        '                </a>' +
        '            </div>' +
        '            <div class="col container-fluid row">' +
        '                <textarea class="form-control col-12 option-min-width alert-success" rows="1" name="right-option" placeholder="Správná možnost" required maxlength="60"></textarea>' +
        wrongOption +
        '             </div>' +
        '             <a class="p-1 wrong-option-creator" href="#" target="_blank" data-toggle="tooltip" title="Přidat další špatnou možnost">' +
        '                 <img src="images/plus.png" class="chevron-icon index-icon">' +
        '             </a>' +
        '             <a class="option-deleter p-1" href="#" target="_blank" data-toggle="tooltip" title="Vymaž podotázku">' +
        '                 <img src="images/garbage.png" class="chevron-icon index-icon">' +
        '   </a>' +
        '</div>';

var connectQuestion =
        '<div class="connectQuestion option row mx-0 mt-2 px-0">' +
        '   <div class="col question-menu mx-0 p-1">' +
        '       <a class="option-up ml-auto px-0" href="#" target = "_blank">' +
        '           <img src = "images/chevron-up.png" class = "chevron-icon index-icon" title="posunout pod-otázku nahoru">' +
        '       </a>' +
        '       <a class="option-down ml-auto px-0" href="#" target = "_blank">' +
        '           <img src = "images/chevron-down.png" class = "chevron-icon index-icon" title="posunout pod-otázku dolů">' +
        '       </a>' +
        '   </div>' +
        '   <textarea class="form-control word col col-12 col-sm-2" rows="1" name="word" placeholder="Pojem" required maxlength="40"></textarea>' +
        '   <textarea class="form-control col container-fluid" rows="1" name="definition" placeholder="Definice" required maxlength="120"></textarea>' +
        '   <a class="p-1 option-deleter" href="#" target = "_blank" data-toggle="tooltip" title="Vymaž možnost">' +
        '       <img src = "images/garbage.png" class = "chevron-icon index-icon">' +
        '   </a>' +
        '</div>';

var orderQuestion =
        '<ol class="order-question option container-fluid row">' +
        '</ol>';

var orderOption =
        '<li class="order-option container-fluid px-0">' +
        '    <div class="container-fluid mx-0 row px-0">' +
        '        <div class="col question-menu mx-0 p-1">' +
        '            <a class="order-option-up ml-auto px-0" href="#" target = "_blank">' +
        '               <img src="images/chevron-up.png" class = "chevron-icon index-icon" title="posunout pod-otázku nahoru">' +
        '            </a>' +
        '            <a class="order-option-down ml-auto px-0" href="#" target = "_blank">' +
        '                <img src="images/chevron-down.png" class = "chevron-icon index-icon" title="posunout pod-otázku dolů">' +
        '            </a>' +
        '        </div>' +
        '        <textarea class="form-control col container-fluid" rows="1" name="word" placeholder="Definice" required maxlength="120"></textarea>' +
        '        <a class="order-option-deleter p-1" href="#" target = "_blank" data-toggle="tooltip" title="Vymaž možnost">' +
        '            <img src="images/garbage.png" class = "chevron-icon index-icon">' +
        '        </a>' +
        '    </div>' +
        '</li>';

function showOptions(optionType) {
    selectedOption = optionType;
    selectedOption.show();
}

function createBooleanQuestion(answerContainer) {
    inputs = $(answerContainer).children('.boolean-question').children('input');
    for (i = 0; i < inputs.length; i++) {
        $(inputs[i]).attr('name', prefix + radioId);
    }
    labels = $(answerContainer).children('.boolean-question').children('label');
    for (i = 0; i < labels.length; i++) {
        $(labels[i]).attr('for', prefix + radioId);
    }
    $(inputs[0]).prop('checked', true);
    radioId++;
}

function moveUp(thisObject) {
    previousObject = $(thisObject).prev();
    $(thisObject).after($(previousObject));
}

function moveDown(thisObject) {
    nextObject = $(thisObject).next();
    $(thisObject).before($(nextObject));
}

function addOrderOption(list) {
    $(list).append(orderOption);
}

function numberOfQuestionsInQuestionDiv(string) {
    var identifier = '___'
    for (var position = 0, count = 0; position >= 0; count++, position += identifier.length) {
        position = string.indexOf(identifier, position);
        if (position == -1) {
            return count;
        }
    }
}

function validateFillInTextQuestion(answerContainer, textContainer) {
    if ($(answerContainer).children('.fill-in-text-question').length != 0) {
        console.log(1);
        if (numberOfQuestionsInQuestionDiv($(textContainer).text()) != $(answerContainer).children().length) {
            if ($(answerContainer).parent().children('.alert').length == 0) {
                $(answerContainer).after(illegalNumberOfOptions);
                $(textContainer).next()[0].setCustomValidity('Počet značek v testu není stejný jako počet možností');
            }
        } else {
            $(answerContainer).parent().children('.alert').remove();
            $(textContainer).next()[0].setCustomValidity('');
        }
    }
}

var illegalNumberOfOptions =
        '<div class="alert alert-danger alert-dismissible fade show mb-0 mt-2" role="alert">' +
        '   <strong>Počet značek v testu není stejný jako počet možností.</strong> Ihned vyrovnejte počet' +
        '   <button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
        '       <span aria-hidden="true">&times;</span>' +
        '   </button>' +
        '</div>';
var radioId = 1;

$(document).ready(function () {
    showTestAction($('#new-test'));
    $('.test-body').html(question);
    $('.answer-container').html(booleanQuestion);
    $($('.answer-container').children('.boolean-question').children('input')[0]).prop('checked', true);

    $('.test-body').on('input', '.question-text-input', function () {
        var answerContainer = $(this).parent().parent().children('.answer-container');
        validateFillInTextQuestion(answerContainer, this);
    });

    $('.test-body').on('click', '.question-deleter', function () {
        var thisQuestion = $(this).parent().parent().parent().parent();
        if (thisQuestion.prev().length != 0 || thisQuestion.next().length != 0) {
            thisQuestion.remove();
        }
    });

    $('.test-body').on('click', '.option-deleter', function () {
        var thisOption = $(this).parent();
        var answerContainer = $(thisOption).parent();
        if (thisOption.prev().length != 0 || thisOption.next().length != 0) {
            thisOption.remove();
        }
        validateFillInTextQuestion(answerContainer, $(answerContainer).prev().children('.question-text-input'));
    });

    $('.test-body').on('click', '.order-option-deleter', function () {
        var thisOption = $(this).parent().parent();
        if (thisOption.prev().length != 0 || thisOption.next().length != 0) {
            thisOption.remove();
        }
    });

    $('.test-body').on('click', '.question-creator', function () {
        var questionContainer = $(this).parent().parent().parent().parent().parent();
        questionContainer.after(question);
        var answerContainer = $(questionContainer.next()).children('.answer-container');
        $(answerContainer).html(booleanQuestion);
        createBooleanQuestion(answerContainer);
        radioId++;
    });

    $('.test-body').on('click', '.option-creator', function () {
        var selected = $(this).parent().parent().children('.question-type-selector').find(':selected').val();
        var answerContainer = $(this).parent().parent().parent().parent().children('.answer-container');
        if (selected == "orderQuestion") {
            addOrderOption($(answerContainer).children('.order-question'));
        } else if (selected != "booleanQuestion") {
            $(answerContainer).append(window[selected]);
            validateFillInTextQuestion(answerContainer, $(this).parent().parent().prev().prev());
        }
    });

    $('.test-body').on('click', '.wrong-option-creator', function () {
        $(this).parent().children('.container-fluid').append(wrongOption);
    });

    $('.test-body').on('click', '.up', function () {
        moveUp($(this).parent().parent().parent().parent());
    });

    $('.test-body').on('click', '.down', function () {
        moveDown($(this).parent().parent().parent().parent());
    });

    $('.test-body').on('click', '.option-up', function () {
        moveUp($(this).parent().parent());
    });

    $('.test-body').on('click', '.option-down', function () {
        moveDown($(this).parent().parent());
    });

    $('.test-body').on('click', '.order-option-up', function () {
        moveUp($(this).parent().parent().parent());
    });

    $('.test-body').on('click', '.order-option-down', function () {
        moveDown($(this).parent().parent().parent());
    });

    $('.test-body').on('change', '.question-type-selector', function () {
        var answerContainer = $(this).parent().parent().parent().children('.answer-container');
        if (this.value == 'booleanQuestion') {
            $(answerContainer).html(booleanQuestion);
            createBooleanQuestion(answerContainer);
        } else if (this.value == 'orderQuestion') {
            $(answerContainer).html(orderQuestion);
            addOrderOption($(answerContainer).children('.order-question'));
        } else {
            if (this.value == 'fillInTextQuestion') {
                $(this).parent().prev().prev().text('___');
            }
            $(answerContainer).html(window[this.value]);
        }
    });

    $('.question-text-input').focusout(function () { //https://stackoverflow.com/questions/20726174/placeholder-for-contenteditable-div
        var element = $(this);
        if (!element.text().trim().length) {
            element.empty();
        }
    });

    $('.question-text-input').on('keydown paste', function (event) { //https://stackoverflow.com/questions/33551502/set-max-length-for-content-editable-element
        var cntMaxLength = parseInt($(this).attr('maxlength'));
        if ($(this).text().length >= cntMaxLength && event.keyCode != 8) {
            event.preventDefault();
        }
    });

    $('.test-creator-content').submit(function (event) {
        $('.question').each(function (i) {
            text = $(this).find('.question-text-input').text();
            if (text.length == 0) {
                alert("Všechny otázky musí být vyplněny");
                event.preventDefault();
                return false;
            }
            $(this).find('.question-text').text(text);
            $(this).find('input, textarea, select').each(function (j) {
                $(this).attr('name', $(this).attr('name') + i);
            });
            $(this).find('textarea[name="word' + i + '"]').each(function (j) {
                $(this).attr('name', $(this).attr('name') + j);
            });
            $(this).find('textarea[name="definition' + i + '"]').each(function (j) {
                $(this).attr('name', $(this).attr('name') + j);
            });
            $(this).find('textarea[name="right-option' + i + '"]').each(function (j) {
                $(this).attr('name', $(this).attr('name') + j);
                $(this).parent().find('textarea[name="wrong-option' + i + '"]').each(function (g) {
                    $(this).attr('name', $(this).attr('name') + j + g);
                });
            });
            return true;
        });
    });
});