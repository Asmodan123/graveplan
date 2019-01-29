var timer;
function onSearchFieldKeyup() {
	clearTimeout(timer);
	timer = setTimeout(searchList, 500);
}

function searchList() {
	var input, filter, ul, trs, a, i, txtValue, style;
	input = document.getElementById("searchInput");
	filter = input.value.toUpperCase();
	var tokens = filter.split(" ");
	ul = document.getElementById("searchResult");
	trs = ul.getElementsByTagName("tr");
	for (i = 0; i < trs.length; i++) {
		txtValue = trs[i].dataset.search;
		style = "";
		for (j = 0; j < tokens.length; j++) {
			if (txtValue.toUpperCase().indexOf(tokens[j]) == -1) {
				style = "none";
				break;
			}
		}
		trs[i].style.display = style;
	}
}