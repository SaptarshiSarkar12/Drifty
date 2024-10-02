import Footer from "../Footer";
import Header from "../Header";

export const metadata = {
  title: "Contact Us",
  description: "Contact the Drifty team for feedback or issues",
};

export default function contact() {
  return (
    <div>
      <Header props={"bg-top"} />
      <div className="select-none bg-gradient-to-b from-top via-about to-bottom ">
        <div className="flex justify-center items-center  w-full max-md:p-2   p-12">
        <form
  className="md:w-3/5 bg-white rounded-2xl shadow-lg shadow-gray-500 p-14"
  method="POST"
  action="https://formsubmit.co/e94b201f9a607081e9f9f8ee09ff5e25"
  aria-label="Contact Us Form"
>
  <h1 className="max-md:text-3xl mb-4 text-5xl font-bold">Contact Us</h1>
  <p>We&apos;d love to hear from you!</p>
  <div className="flex justify-between gap-2 mt-6  max-md:flex-col">
      <input
        type="text"
        autoFocus={true}
        inputMode="text"
        required
        placeholder="Name"
        pattern="^[a-zA-Z_]{5,}$"
        title="Please enter at least 5 characters. Only lowercase, uppercase and underscore are allowed"
        className="h-[50px] border pl-3 min-w-[200px] max-md:w-[95%] w-[40%]  border-gray-300 rounded-3xl"
      />
      <input
        className="h-[50px] border pl-3  min-w-[200px] max-md:w-[95%]  w-[40%] max-w-[500px] border-gray-300 rounded-3xl"
        type="email"
        required
        pattern="^[A-Za-z\s'-]{5,}$"
        id="email"
        autoFocus
        placeholder="Email"
        title="Please enter a valid email address (e.g., example@domain.com)."
      />
  </div>
  <input
  type="tel"
  autoFocus={true}
  required
  className="border rounded-full p-2 w-full my-2 bg-white focus:outline-blue-600 outline:none caret-blue-600 mt-6 md:mr-auto md:ml-1 justify-start h-12 md:col-span-1 col-span-2 text-black"
  pattern="^[+0-9_]{10,}$"
  inputMode="tel"  // Changed to "tel"
  title="Please enter a valid phone number. Format: +CountryCode (1-3 digits) followed by a 10-digit number (e.g., +123 4567890123) or a 10-digit local number (e.g., 1234567890)."
  placeholder="Phone Number"
/>
  <textarea
    className="border rounded-lg w-full my-2 focus:outline-blue-600 outline:none caret-blue-600 min-h-[150px] col-span-2 mt-6 p-4 text-black"
    inputMode="text"
    name="message"
    placeholder="How Can I Help You?"
    required
  ></textarea>
  
  <button
    className="rounded-3xl mt-10 hover:bg-yellow-400 hover:text-gray-800 hover:shadow-lg active:shadow-lg w-full bg-blue-600 text-white p-3 ease-in-out"
    type="submit"
    id="submit"
  >
    Send Message
  </button>
  
  <p className="text-lg mt-10 text-center">
    By contacting us you agree to our <b>Terms of Service</b> and <b>Privacy Policy</b>.
  </p>
</form>

        </div>
      </div>
      <Footer />
    </div>
  );
}
