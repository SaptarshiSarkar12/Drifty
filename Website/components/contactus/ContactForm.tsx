"use client";
import { useState } from "react";
import LoadingSpinner from "./LoadingSpinner";
import { submitContactForm } from "./submitForm";

export default function ContactForm() {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitStatus, setSubmitStatus] = useState<"success" | "error" | null>(
    null
  );

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setIsSubmitting(true);
    setSubmitStatus(null);

    const formData = new FormData(event.currentTarget);
    const res = await submitContactForm(formData);
    try {
      if (res.success) setSubmitStatus("success");
      setSubmitStatus("success");
    } catch (error) {
      setSubmitStatus("error");
      console.debug(error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="p-6 rounded-lg shadow-lg">
      <h2 className="text-2xl font-semibold mb-4">Message Us Anytime</h2>
      <form className="space-y-4" onSubmit={handleSubmit}>
        <div>
          <label htmlFor="name" className="block text-sm font-medium">
            Name
          </label>
          <input
            type="text"
            id="name"
            name="name"
            placeholder="What should we call you?"
            className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            required
          />
        </div>
        <div>
          <label htmlFor="email" className="block text-sm font-medium">
            Email
          </label>
          <input
            type="email"
            id="email"
            name="email"
            placeholder="Where can we reach you?"
            className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            required
          />
        </div>
        <div>
          <label htmlFor="message" className="block text-sm font-medium">
            Message
          </label>
          <textarea
            id="message"
            name="message"
            placeholder="Drop your thoughts here!"
            rows={2}
            className="mt-1 block w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            required
          ></textarea>
        </div>
        <p className="text-center opacity-70">
          By contacting us, you agree to our{" "}
          <a
            href="/terms"
            className="font-semibold underline underline-offset-2 hover:text-blue-400"
          >
            Terms of Service
          </a>{" "}
          and{" "}
          <a
            href="/privacy-policy"
            className="font-semibold underline underline-offset-2 hover:text-blue-400"
          >
            Privacy Policy
          </a>
          .
        </p>
        <button
          type="submit"
          disabled={isSubmitting}
          className="button w-full py-2 px-4 rounded-md font-semibold hover:outline-none hover:ring-2 hover:ring-blue-500 hover:ring-offset flex items-center justify-center"
        >
          {isSubmitting ? <LoadingSpinner /> : "Send Message"}
        </button>
        {submitStatus === "success" && (
          <div className="mt-4 p-4 bg-green-100 dark:bg-green-800 text-green-800 dark:text-green-100 rounded-md">
            Message sent successfully!
          </div>
        )}
        {submitStatus === "error" && (
          <div className="mt-4 p-4 bg-red-100 dark:bg-red-800 text-red-800 dark:text-red-100 rounded-md">
            Failed to send message. Please try again.
          </div>
        )}
      </form>
    </div>
  );
}
