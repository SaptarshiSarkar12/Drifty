import ContactForm from "../../components/contactus/ContactForm";
import FAQs from "../../components/contactus/faq";
import SocialLinks from "../../components/contactus/SocialLinks";

export default function Contact() {
  return (
    <div className="h-auto py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="w-full inline-flex justify-center mb-4">
          <span className="inline-flex justify-center items-center rounded-xl bg-gray-700 px-3 py-1 text-xs font-medium text-gray-100 ring-1 ring-blue-700/10 ring-inset">
            Contact Us
          </span>
        </div>
        <h1 className="text-4xl font-bold text-center mb-8">
          Let&#39;s Get In Touch
        </h1>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
          <ContactForm />
          <div className="space-y-8">
            <SocialLinks />
            <FAQs />
          </div>
        </div>
      </div>
    </div>
  );
}
