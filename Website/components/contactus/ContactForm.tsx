"use client";
import { useState, useEffect } from "react";
import LoadingSpinner from "./LoadingSpinner";
import { submitContactForm } from "./submitForm";

export default function ContactForm() {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    message: "",
  });
  const [errors, setErrors] = useState({ name: "", email: "", message: "" });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitStatus, setSubmitStatus] = useState<"success" | "error" | null>(
    null,
  );

  useEffect(() => {
    if (submitStatus === "success") {
      const timer = setTimeout(() => setSubmitStatus(null), 5000);
      return () => clearTimeout(timer);
    }
  }, [submitStatus]);

  const validateForm = () => {
    let isValid = true;
    const newErrors = { name: "", email: "", message: "" };

    if (!formData.name.trim()) {
      newErrors.name = "Name is required.";
      isValid = false;
    }
    if (!formData.email.trim()) {
      newErrors.email = "Email is required.";
      isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = "Invalid email format.";
      isValid = false;
    }
    if (!formData.message.trim()) {
      newErrors.message = "Message is required.";
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!validateForm()) return;
    setIsSubmitting(true);
    const res = await submitContactForm(new FormData(e.currentTarget));
    setSubmitStatus(res.success ? "success" : "error");
    if (res.success) setFormData({ name: "", email: "", message: "" });
    setIsSubmitting(false);
  };

  return (
    <div className="p-6 rounded-lg shadow-lg">
      <h2 className="text-2xl font-semibold mb-4">Message Us Anytime</h2>
      <form className="space-y-4" onSubmit={handleSubmit} noValidate>
        {["name", "email", "message"].map((field) => (
          <div key={field}>
            <label
              htmlFor={field}
              className="block text-sm font-medium capitalize"
            >
              {field}
            </label>
            {field === "message" ? (
              <textarea
                id={field}
                name={field}
                value={formData[field as keyof typeof formData]}
                onChange={handleChange}
                placeholder={`Enter your ${field}`}
                rows={2}
                className="mt-1 block w-full px-3 py-2 border border-(--light-accent) dark:border-(--dark-accent) rounded-md shadow-sm focus:outline-none focus:ring-blue-500"
              />
            ) : (
              <input
                type={field === "email" ? "email" : "text"}
                id={field}
                name={field}
                value={formData[field as keyof typeof formData]}
                onChange={handleChange}
                placeholder={`Enter your ${field}`}
                className="mt-1 block w-full px-3 py-2 border border-(--light-accent) dark:border-(--dark-accent) rounded-md shadow-sm focus:outline-none focus:ring-blue-500"
              />
            )}
            {errors[field as keyof typeof errors] && (
              <p className="text-red-500 text-sm mt-1">
                {errors[field as keyof typeof errors]}
              </p>
            )}
          </div>
        ))}
        <button
          type="submit"
          disabled={isSubmitting}
          className="button w-full py-2 px-4 rounded-md font-semibold flex items-center justify-center hover:ring-2 hover:ring-blue-500"
        >
          {isSubmitting ? <LoadingSpinner /> : "Send Message"}
        </button>
        {submitStatus && (
          <div
            className={`mt-4 p-4 rounded-md ${
              submitStatus === "success"
                ? "bg-green-100 text-green-800"
                : "bg-red-100 text-red-800"
            }`}
          >
            {submitStatus === "success"
              ? "Message sent successfully!"
              : "Failed to send message. Please try again."}
          </div>
        )}
      </form>
    </div>
  );
}
