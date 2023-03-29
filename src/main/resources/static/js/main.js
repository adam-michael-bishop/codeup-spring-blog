"use strict";
$(function () {
	function resetSubmitButton() {
		$("#form-submit").text("Save Changes").removeClass().addClass(["btn", "btn-primary"]).prop("disabled", false);
	}

	function submitForm(data) {
		return $.ajax({
			url: "/posts/save",
			type: "POST",
			data: data
		})
	}

	function showSavingButton() {
		$("#form-submit").html(`<span class="spinner-border spinner-border-sm"></span> Saving...`).prop("disabled", true);
	}

	$("#edit-post-form").on("submit", function (e) {
		e.preventDefault();
		showSavingButton();
		submitForm($(this).serialize())
			.done(function () {
				$("#form-submit").text("Changes Saved").addClass("btn-success").removeClass("btn-primary")
			})
			.fail(function () {
				$("#form-submit").text("Epic Failure").addClass("btn-danger").removeClass("btn-primary")
			});
	}).find(":input").each(function () {
		$(this).on("input propertychange paste", resetSubmitButton);
	});

	$("#create-post-form").on("submit", function (e) {
		e.preventDefault();
		showSavingButton();
		submitForm($(this).serialize())
			.done(function () {
				resetSubmitButton();
				location.reload();
			})
			.fail(function () {
				$("#form-submit").text("Epic Failure").addClass("btn-danger").removeClass("btn-primary")
			});
	})
});