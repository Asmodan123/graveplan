var x = document.getElementsByClassName("ref");
  for (var i = 0; i < x.length; i++) {
    x[i].addEventListener('click', 
    	function(e) {
    	    var owner = e.target.getAttribute("data-owner");
    	    if (owner != null) {
		alert(owner.replace(/;/g,"\n"));
    	    } 
	});
  }
