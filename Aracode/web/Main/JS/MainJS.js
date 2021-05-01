var browserHeight = $(window).height();
var navbar = $('nav');
var minSectionHeight = $('#obligatoryTests').css('height');

$('body').css('padding-top', navbarHeight());
//var first = $('#first');
//first.css('min-height', minSectionHeight);
//var second = $('#second');
//second.css('min-height', minSectionHeight);

//var myTable = document.getElementsByTagName("table")[0];
//myClone = myTable.cloneNode(true);
//procvicovani.append(myClone);

$(document).on('click', 'a[href^="#"]', function (event) { //https://stackoverflow.com/questions/47490041/jquery-not-working-when-i-want-to-make-smooth-scroll-to-the-sections
    event.preventDefault();
    $('html, body').animate({
        scrollTop: $($.attr(this, 'href')).offset().top - navbarHeight()
    }, 500);
});

function navbarHeight(){
    return parseFloat(navbar.height()) + parseFloat(navbar.css('padding-top')) + parseFloat(navbar.css('padding-bottom'));
}