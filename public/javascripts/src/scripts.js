$(function($) {
	setTimeout(function() {
		$('#content-wrapper > .row').css({
			opacity: 1
		});
	}, 200);

	//Active state setup on sidebar menu
	var actionPath = window.location.pathname;

	$('#sidebar-nav,#nav-col-submenu').on('click', '.dropdown-toggle', function (e) {
		e.preventDefault();
		
		var $item = $(this).parent();

		if (!$item.hasClass('open')) {
			$item.parent().find('.open .submenu').slideUp('fast');
			$item.parent().find('.open').toggleClass('open');
		}
		
		$item.toggleClass('open');
		
		if ($item.hasClass('open')) {
			$item.children('.submenu').slideDown('fast');
		} 
		else {
			$item.children('.submenu').slideUp('fast');
		}
	});

	//Export as csv
	$('.csv-export').click(function (e) {
		var tableId = $(this).attr("href");
		var table = $(tableId);
		var csvData = table.table2CSV({
			separator: ',',
			header: [],
			delivery: 'data'
		});

		//Create another a tag with a download attr and click it
		var pom = document.createElement('a');
		pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(csvData));
		pom.setAttribute('download', $(this).data("filename") + ".csv");

		pom.style.display = 'none';
		document.body.appendChild(pom);

		pom.click();

		document.body.removeChild(pom);
	});

	//Mask phone numbers
	$('form input[name="phoneNumber"]').mask("(999) 999-9999");
	$('#daily-report-datepicker').datepicker().on("changeDate", function(e) {
		var url = "/reports/daily-referrals?date=";
		url += e.date.getTime();
		window.location.href = url;
	});
	$('#timepicker').timepicker();

	$('body').on('mouseenter', '#page-wrapper.nav-small #sidebar-nav .dropdown-toggle', function (e) {
		if ($( document ).width() >= 992) {
			var $item = $(this).parent();

			if ($('body').hasClass('fixed-leftmenu')) {
				var topPosition = $item.position().top;

				if ((topPosition + 4*$(this).outerHeight()) >= $(window).height()) {
					topPosition -= 6*$(this).outerHeight();
				}

				$('#nav-col-submenu').html($item.children('.submenu').clone());
				$('#nav-col-submenu > .submenu').css({'top' : topPosition});
			}

			$item.addClass('open');
			$item.children('.submenu').slideDown('fast');
		}
	});
	
	$('body').on('mouseleave', '#page-wrapper.nav-small #sidebar-nav > .nav-pills > li', function (e) {
		if ($( document ).width() >= 992) {
			var $item = $(this);
	
			if ($item.hasClass('open')) {
				$item.find('.open .submenu').slideUp('fast');
				$item.find('.open').removeClass('open');
				$item.children('.submenu').slideUp('fast');
			}
			
			$item.removeClass('open');
		}
	});
	$('body').on('mouseenter', '#page-wrapper.nav-small #sidebar-nav a:not(.dropdown-toggle)', function (e) {
		if ($('body').hasClass('fixed-leftmenu')) {
			$('#nav-col-submenu').html('');
		}
	});
	$('body').on('mouseleave', '#page-wrapper.nav-small #nav-col', function (e) {
		if ($('body').hasClass('fixed-leftmenu')) {
			$('#nav-col-submenu').html('');
		}
	});
	
	$('#make-small-nav').click(function (e) {
		$('#page-wrapper').toggleClass('nav-small');
	});
	
	$(window).smartresize(function(){
		if ($( document ).width() <= 991) {
			$('#page-wrapper').removeClass('nav-small');
		}
	});
	
	$('.mobile-search').click(function(e) {
		e.preventDefault();
		
		$('.mobile-search').addClass('active');
		$('.mobile-search form input.form-control').focus();
	});
	$(document).mouseup(function (e) {
		var container = $('.mobile-search');

		if (!container.is(e.target) // if the target of the click isn't the container...
			&& container.has(e.target).length === 0) // ... nor a descendant of the container
		{
			container.removeClass('active');
		}
	});
	
	$('.fixed-leftmenu #col-left').nanoScroller({
    	alwaysVisible: false,
    	iOSNativeScrolling: false,
    	preventPageScrolling: true,
    	contentClass: 'col-left-nano-content'
    });
	
	// build all tooltips from data-attributes
	$("[data-toggle='tooltip']").each(function (index, el) {
		$(el).tooltip({
			placement: $(this).data("placement") || 'top'
		});
	});

});

$.fn.removeClassPrefix = function(prefix) {
    this.each(function(i, el) {
        var classes = el.className.split(" ").filter(function(c) {
            return c.lastIndexOf(prefix, 0) !== 0;
        });
        el.className = classes.join(" ");
    });
    return this;
};

(function($,sr){
	// debouncing function from John Hann
	// http://unscriptable.com/index.php/2009/03/20/debouncing-javascript-methods/
	var debounce = function (func, threshold, execAsap) {
		var timeout;

		return function debounced () {
			var obj = this, args = arguments;
			function delayed () {
				if (!execAsap)
					func.apply(obj, args);
				timeout = null;
			};

			if (timeout)
				clearTimeout(timeout);
			else if (execAsap)
				func.apply(obj, args);

			timeout = setTimeout(delayed, threshold || 100);
		};
	}
	// smartresize 
	jQuery.fn[sr] = function(fn){	return fn ? this.bind('resize', debounce(fn)) : this.trigger(sr); };

})(jQuery,'smartresize');