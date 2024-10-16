Behaviour.specify(".vjf-regex-select", "vjf-regex-select", 0, function (e) {
  e.addEventListener("change", function () {
    const nameOptions = e.closest(".valueType").querySelector(".nameOptions");
    nameOptions.classList.toggle("jenkins-hidden", !e.options[e.selectedIndex].value.match(/NAME/))
  });
});
