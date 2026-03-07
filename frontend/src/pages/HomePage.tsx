import { Link } from "react-router";

const HomePage = () => {
    return (
        <div className="min-h-screen bg-base-200 flex items-center justify-center px-4">
            <div className="w-full max-w-lg bg-base-100 border border-base-300 rounded-2xl p-8 text-center">
                <h1 className="text-2xl font-bold text-base-content">Slotly</h1>
                <p className="mt-2 text-sm text-base-content/60">
                    Schedule meetings, share booking links, and manage availability.
                </p>
                <div className="mt-6 flex items-center justify-center gap-3">
                    <Link to="/login" className="btn btn-primary btn-sm">
                        Sign in
                    </Link>
                    <Link to="/register" className="btn btn-outline btn-sm">
                        Create account
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
