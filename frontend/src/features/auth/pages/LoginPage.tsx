import { GoogleLogin } from "@react-oauth/google";
import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { useAuth } from "../hooks/useAuth";
import toast from "react-hot-toast";
import axios from "axios";

const LoginPage = () => {
    const { login, loginWithGoogle } = useAuth();
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const handleLogin = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        setIsLoading(true);

        try {
            await login(email, password);
            navigate("/scheduling");
        } catch (error) {
            if (axios.isAxiosError(error)) {
                toast.error(error.response?.data?.message);
            } else {
                toast.error("Something went wrong.");
            }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-base-200 flex items-center justify-center px-4">
            <div className="w-full max-w-sm flex flex-col gap-6">
                {/* Card */}
                <div className="bg-base-100 rounded-2xl shadow-sm border border-base-300 p-8 flex flex-col gap-5">
                    {/* Form */}
                    <form
                        onSubmit={handleLogin}
                        className="flex flex-col gap-4"
                    >
                        <div className="flex flex-col gap-1.5">
                            <label className="text-sm font-medium text-base-content">
                                Email
                            </label>
                            <input
                                type="email"
                                className="input input-bordered w-full"
                                placeholder="you@example.com"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                            />
                        </div>

                        <div className="flex flex-col gap-1.5">
                            <div className="flex items-center justify-between">
                                <label className="text-sm font-medium text-base-content">
                                    Password
                                </label>
                                <a
                                    href="/forgot-password"
                                    className="text-xs text-primary hover:underline"
                                >
                                    Forgot password?
                                </a>
                            </div>
                            <input
                                type="password"
                                className="input input-bordered w-full"
                                placeholder="••••••••"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary w-full mt-1"
                            disabled={isLoading}
                        >
                            {isLoading ? (
                                <span className="loading loading-spinner loading-sm" />
                            ) : (
                                "Sign in"
                            )}
                        </button>
                    </form>

                    {/* Divider */}
                    <div className="divider text-xs text-base-content/40">
                        or
                    </div>

                    {/* Google */}
                    <GoogleLogin
                        onSuccess={(res) => {
                            loginWithGoogle(res.credential!)
                                .then(() => navigate("/scheduling"))
                                .then(() =>
                                    toast.success("Successfully signed in"),
                                )
                                .catch(() =>
                                    toast.error("Google sign-in failed."),
                                );
                        }}
                        onError={() => toast.error("Google sign-in failed.")}
                    />
                </div>

                {/* Register link */}
                <p className="text-center text-sm text-base-content/50">
                    Don't have an account?{" "}
                    <Link
                        to="/register"
                        className="text-primary font-medium hover:underline"
                    >
                        Sign up
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default LoginPage;
