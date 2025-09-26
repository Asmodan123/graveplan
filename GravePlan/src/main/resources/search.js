var timer;
function onSearchFieldKeyup() {
	clearTimeout(timer);
	timer = setTimeout(searchList, 500);
}

function searchList() {
	var input, filter, ul, trs, tokens, i, txtValue, style, maxTokenLength;
	input = document.getElementById("searchInput");
	filter = input.value.toUpperCase().trim();
	tokens = filter.split(" ");
	ul = document.getElementById("searchResult");
	trs = ul.getElementsByTagName("tr");
	for (i = 0; i < trs.length; i++) {
		txtValue = trs[i].dataset.search;
		style = "none";
		if (filter.length > 2) {
			style = "";
			maxTokenLength = 0;
			for (j = 0; j < tokens.length; j++) {
				if (txtValue.indexOf(tokens[j]) === -1) {
					style = "none";
					break;
				}
				if (tokens[j].length > maxTokenLength) {
					maxTokenLength = tokens[j].length;
				}
			}
			if (maxTokenLength < 3) {
				style = "none";
			}
		}
		trs[i].style.display = style;
	}
}