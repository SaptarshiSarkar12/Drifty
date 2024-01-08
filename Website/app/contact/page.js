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
      <div className="select-none bg-gradient-to-b from-top via-about to-bottom py-5">
        <h1 className=" text-center font-semibold text-7xl">Contact Us</h1>
        <p className="text-center font-normal pt-5 pb-8 text-lg">
          We`d love to hear from you!
        </p>
        <form
          className="grid grid-cols-2 justify-items-center"
          method="POST"
          action="https://formsubmit.co/e94b201f9a607081e9f9f8ee09ff5e25"
        >
          <input
            className="bg-white focus:outline-blue-600 text-blue-600 outline:none caret-blue-600 md:w-6/12 sm:w-1/2 h-14 p-3 col-span-2"
            type="text"
            name="name"
            inputMode="text"
            required
            placeholder="Name"
          />
          <input
            className="bg-white focus:outline-blue-600 outline:none caret-blue-600 mt-10 md:ml-auto md:w-1/2 justify-end h-14 md:col-span-1 col-span-2 p-2 text-blue-600"
            inputMode="email"
            pattern="/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/"
            type="email"
            name="email"
            required
            placeholder="Email"
          />
          <input
            className="bg-white focus:outline-blue-600 outline:none caret-blue-600 mt-10 md:mr-auto md:ml-1 md:w-1/2 justify-start  h-14 p-2  md:col-span-1 col-span-2 text-blue-600"
            minLength={6}
            pattern="/^([+]?\d{1,2}[-\s]?|)\d{3}[-\s]?\d{3}[-\s]?\d{4}$/"
            maxLength={26}
            inputMode="tel"
            type="tel"
            name="phone"
            required
            placeholder="Phone Number"
          />
          <textarea
            className="focus:outline-blue-600 outline:none caret-blue-600 md:w-6/12 sm:w-1/2 h-64 col-span-2 mt-10 p-6  text-blue-600 resize-none"
            inputMode="text"
            pattern="text"
            name="message"
            placeholder="Message"
            required
          ></textarea>
          <button className="mt-10 px-6 col-span-2 py-2 w-30 h-10 bg-blue-700 text-white font-medium text-lg leading-tight rounded-lg shadow-md hover:bg-yellow-400 hover:shadow-lg active:bg-blue-400 active:shadow-lg transition duration-100 ease-in-out">
            Send Message
          </button>
        </form>
      </div>
      <Footer />
    </div>
  );
}
