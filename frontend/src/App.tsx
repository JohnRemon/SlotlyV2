import { Route, Routes } from "react-router";
import DashBoard from "./pages/dashboard-page.tsx";
import Meetings from "./pages/meetings-page";
import Availability from "./pages/availability-page.tsx";
import Profile from "./pages/profile-page.tsx";
import { Settings } from "lucide-react";

function App() {
    return (
        <div>
            <Routes>
                <Route element={<DashBoard />}>
                    <Route path="/meetings" element={<Meetings />} />
                    <Route path="/availability" element={<Availability />} />
                    <Route path="/profile" element={<Profile />} />
                    <Route path="/settings" element={<Settings />} />
                </Route>
            </Routes>
        </div>
    );
}

export default App;
