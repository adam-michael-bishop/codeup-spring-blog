"use strict";
$(function () {
	let messageFade;

	function alert(message, type) {
		$("#alert-wrapper").html(`
			<div class="alert alert-${type} alert-dismissible" role="alert">${message}
			<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
			</div>`);
	}

	function sendSuccessMessage() {
		$("#submission-message").text("Changes have been saved.").removeClass().addClass("text-success");
		messageFade = setTimeout(function () {
			$("#submission-message").text("").removeClass();
		}, 5000);
	}

	function resetSubmitButton() {
		$("#form-submit").text("Save Changes").removeClass().addClass(["btn", "btn-primary"]).prop("disabled", false);
	}

	$("#edit-post-form, #create-post-form").each(function () {
		$(this).on("submit", function (e) {
			const data = $(this).serialize();
			$("#form-submit").html(`<span class="spinner-border spinner-border-sm"></span> Saving...`).prop("disabled", true);
			$.ajax({
				url: "/posts/save",
				type: "POST",
				data: data
			}).done(function () {
				$("#form-submit").text("Changes Saved").addClass("btn-success").removeClass("btn-primary")
			}).fail(function () {
				$("#form-submit").text("Epic Failure").addClass("btn-danger").removeClass("btn-primary")
			});
			e.preventDefault();
		}).find(":input").each(function () {
			$(this).on("input propertychange paste", resetSubmitButton);
		});
	});
});