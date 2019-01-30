var timer;
function onSearchFieldKeyup() {
	clearTimeout(timer);
	timer = setTimeout(searchList, 500);
}

function searchList() {
	var input, filter, ul, trs, tokens, i, txtValue, style;
	input = document.getElementById("searchInput");
	filter = input.value.toUpperCase().trim();
	tokens = filter.split(" ");
	ul = document.getElementById("searchResult");
	trs = ul.getElementsByTagName("tr");
	for (i = 0; i < trs.length; i++) {
		txtValue = trs[i].dataset.search;
		style = "none";
		for (j = 0; j < tokens.length; j++) {
			if (tokens[j].length > 2
					&& txtValue.toUpperCase().indexOf(tokens[j]) > -1) {
				style = "";
				break;
			}
		}
		trs[i].style.display = style;
	}
}