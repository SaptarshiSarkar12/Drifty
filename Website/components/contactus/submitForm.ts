"use server";
import sendToDiscord from "@/utils/sendToDiscord";

export async function submitContactForm(formData: FormData) {
  const name = formData.get("name") as string;
  const email = formData.get("email") as string;
  const message = formData.get("message") as string;

  if (!name || !email || !message) {
    return { success: false, error: "All fields are required" };
  }

  try {
    await sendToDiscord(name, email, message);
    return { success: true };
  } catch (e) {
    console.error("Server Error: Failed to send message " + e);
    return { success: false, error: "Failed to send message" };
  }
}
