"use strict";
$(function () {
	function resetSubmitButton() {
		$("#form-submit").text("Save Changes").removeClass().addClass(["btn", "btn-primary"]).prop("disabled", false);
	}

	function submitForm(data, url) {
		return $.ajax({
			url: url,
			type: "POST",
			data: data
		})
	}

	function showSavingButtonSpinner() {
		$("#form-submit").html(`<span class="spinner-border spinner-border-sm"></span> Saving...`).prop("disabled", true);
	}

	function setServerErrorText() {
		$("#submission-message").text("Epic failure has occurred. A team of genetically enhanced hamsters is busy working on the problem now.")
		$("#form-submit").text("Resubmit").addClass("btn-warning").removeClass("btn-primary").prop("disabled", false)
	}

	$("#edit-post-form").on("submit", function (e) {
		e.preventDefault();
		showSavingButtonSpinner();
		submitForm($(this).serialize(), "/posts/save")
			.done(function () {
				$("#form-submit").text("Changes Saved").addClass("btn-success").removeClass("btn-primary");
				$("#edit-post-modal").on("hide.bs.modal", location.reload.bind(location));
			})
			.fail(function () {
				setServerErrorText();
			});
	}).find(":input").each(function () {
		$(this).on("input propertychange paste", resetSubmitButton);
	});

	$("#create-post-form").on("submit", function (e) {
		e.preventDefault();
		showSavingButtonSpinner();
		submitForm($(this).serialize(), "/posts/create")
			.done(function () {
				resetSubmitButton();
				location.reload();
			})
			.fail(function () {
				setServerErrorText();
			});
	});
});