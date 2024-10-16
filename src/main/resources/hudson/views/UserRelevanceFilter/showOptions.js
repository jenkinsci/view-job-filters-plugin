function showOrHideBuildOptions(elem) {
  var jobFilter = elem.closest('[name="jobFilters"]');
  var matchBuilder = jobFilter.querySelector('[name="matchBuilder"]');
  var matchScmChanges = jobFilter.querySelector('[name="matchScmChanges"]');

  var tr2 = jobFilter.querySelector('[name="buildCountTypeString"]').parentElement.parentElement.parentElement;
  var tr3 = jobFilter.querySelector('[name="amountTypeString"]').parentElement.parentElement.parentElement;

  if (matchBuilder.checked || matchScmChanges.checked) {
    tr2.classList.remove("jenkins-hidden");
    tr3.classList.remove("jenkins-hidden");
  } else {
    tr2.classList.add("jenkins-hidden");
    tr3.classList.add("jenkins-hidden");
  }
}

Behaviour.specify(".showOrHideBuildOptions", "showOrHideBuildOptions", 0, function (element) {
  element.addEventListener("click", function () {
    showOrHideBuildOptions(element);
  };
  showOrHideBuildOptions(element);
});
