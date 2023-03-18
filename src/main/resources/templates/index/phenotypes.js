activeItem = $('.first-item');

function setActive(e) {

  if (activeItem){
    activeItem.removeClass("active");
  }
  $(this).addClass("active");
  activeItem = $(this);

  var linkPlot = $(this).data("link-plot");
  var linkReport = $(this).data("link-report");

  console.log(linkPlot);
  console.log(linkReport);
  $('#tab-plot').attr("href", linkPlot);
  $('#tab-report').attr("href", linkReport);
  $('#tab-plot').addClass("active");
  $('#tab-report').removeClass("active");
}



function filterPhenotypes(e) {
  var input = $(this).val()
  var filter = input.toUpperCase()
  $('.list-group .list-group-item').each(function() {
    var anchor = $(this)
    if (anchor.data('meta') == undefined || anchor.data('meta').toUpperCase().indexOf(filter) > -1) {
      anchor.removeClass('d-none')
    } else {
      anchor.addClass('d-none');
    }
  });
}


$(document).ready(function() {

  //event handler
  $('.list-group-item').on('click', setActive);
  $('#tab-plot').on('click', function(){
    $('#tab-plot').addClass("active");
    $('#tab-report').removeClass("active");
  });
  $('#tab-report').on('click', function(){
    $('#tab-report').addClass("active");
    $('#tab-plot').removeClass("active");
  });
  $('#s').on('input', filterPhenotypes);


  console.log("Ready to explore data :)");
  
});
