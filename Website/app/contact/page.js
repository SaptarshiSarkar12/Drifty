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
          className="md:w-3/5   bg-white rounded-2xl shadow-lg shadow-gray-500 p-14" 
          method="POST"
          action="https://formsubmit.co/e94b201f9a607081e9f9f8ee09ff5e25"
          aria-label="Contact Us Form"
        >
                      <h1 className="  max-md:text-3xl mb-4 text-5xl font-bold">Contact Us</h1>
                      <p className="">          We&apos;d love to hear from you!
                      </p>
                      <div className="flex justify-around gap-2 mt-6 max-md:flex-col">
          <input
            className= "border max-md:w-full rounded-full max-w-[400px] p-2 w-1/2 bg-white  focus:outline-blue-600 text-blue-600 outline:none caret-blue-600 md:w-6/12 sm:w-1/2  h-12  col-span-2"
            type="text"
            name="name"
            autoFocus={true}
            inputMode="text"
            required
            placeholder="Name"
            pattern="^[A-Za-z\s]{1,}$"
          />
          <input 
            className="border max-md:w-full max-md:mt-6  max-w-[400px] rounded-full p-2 w-1/2 bg-white  focus:outline-blue-600 outline:none caret-blue-600  h-12  md:ml-auto md:w-1/2 justify-end  md:col-span-1 col-span-2  text-blue-600"
            inputMode="email"
            pattern="^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
            type="email"
            name="email"
            required
            placeholder="Email"
          />
          </div>
          <input
            className="border rounded-full p-2 w-full my-2 bg-white  focus:outline-blue-600 outline:none caret-blue-600 mt-6 md:mr-auto md:ml-1 justify-start  h-12   md:col-span-1 col-span-2 text-blue-600"
            minLength={6}
            pattern="^[+]?[(]?[0-9]{3}[)]?[-\s.]?[0-9]{3}[-\s.]?[0-9]{4,6}$"
            maxLength={26}
            inputMode="tel"
            type="tel"
            name="phone"
            required
            placeholder="Phone Number"
          />
          <textarea
            className="border rounded-lg w-full my-2  focus:outline-blue-600 outline:none caret-blue-600  min-h-[150px] col-span-2 mt-6 p-4  text-blue-600 "
            inputMode="text"
            pattern="text"
            name="message"
            placeholder="How Can I Help You ?"
            required
          ></textarea>
          <button className=" rounded-3xl mt-10 hover:bg-yellow-400 hover:text-gray-800 hover:shadow-lg active:shadow-lg w-full bg-blue-600 text-white p-3   ease-in-out">
            Send Message
          </button>
          <p className="text-lg mt-10 text-center ">By contacting us you agree to our <b>Terms of Service</b> and <b>Privacy Policy</b>.</p>

        </form>
        </div>
      </div>
      <Footer />
    </div>
  );
}
