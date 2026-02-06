import "./App.css";
import { GoogleSignInButton } from "./components/auth/GoogleSignInButton";

function App() {
  return (
    <>
      <div
        style={{ marginTop: "20px", maxWidth: "300px", margin: "20px auto" }}
      >
        <GoogleSignInButton />
      </div>
    </>
  );
}

export default App;
