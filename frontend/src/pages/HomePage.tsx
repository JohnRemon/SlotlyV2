import { useEffect, useRef } from "react";
import { Link } from "react-router";
import "../HomePage.css";

const SLOTS = [
    "9:00 AM",
    "9:30 AM",
    "10:00 AM",
    "10:30 AM",
    "11:00 AM",
    "2:00 PM",
];
const DAY_LABELS = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"];

const HomePage = () => {
    const calLabelsRef = useRef<HTMLDivElement>(null);
    const calRef = useRef<HTMLDivElement>(null);
    const slotsRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const els = document.querySelectorAll(".reveal");
        const obs = new IntersectionObserver(
            (entries) =>
                entries.forEach((e) => {
                    if (e.isIntersecting) e.target.classList.add("visible");
                }),
            { threshold: 0.15 },
        );
        els.forEach((el) => obs.observe(el));

        if (!calRef.current || !slotsRef.current || !calLabelsRef.current)
            return;

        // render day labels
        DAY_LABELS.forEach((d) => {
            const el = document.createElement("div");
            el.className = "mock-day-label";
            el.textContent = d;
            calLabelsRef.current!.appendChild(el);
        });

        const today = new Date();
        const daysInMonth = new Date(
            today.getFullYear(),
            today.getMonth() + 1,
            0,
        ).getDate();
        const firstDay = new Date(
            today.getFullYear(),
            today.getMonth(),
            1,
        ).getDay();
        let selected = Math.min(today.getDate() + 2, daysInMonth);
        let pickedSlot = "10:00 AM";

        const renderSlots = () => {
            slotsRef.current!.innerHTML = "";
            SLOTS.forEach((s) => {
                const el = document.createElement("button");
                el.textContent = s;
                el.className = `mock-slot${s === pickedSlot ? " picked" : ""}`;
                el.addEventListener("click", () => {
                    pickedSlot = s;
                    renderSlots();
                });
                slotsRef.current!.appendChild(el);
            });
        };

        const renderCal = () => {
            calRef.current!.innerHTML = "";
            for (let i = 0; i < firstDay; i++) {
                calRef.current!.appendChild(document.createElement("div"));
            }
            for (let d = 1; d <= daysInMonth; d++) {
                const el = document.createElement("button");
                el.textContent = String(d);
                el.className = `mock-day${d < today.getDate() ? " past" : d === selected ? " selected" : " available"}`;
                if (d >= today.getDate()) {
                    el.addEventListener("click", () => {
                        selected = d;
                        renderCal();
                        renderSlots();
                    });
                }
                calRef.current!.appendChild(el);
            }
        };

        renderCal();
        renderSlots();
        return () => obs.disconnect();
    }, []);

    return (
        <div className="lp-root">
            {/* NAV */}
            <nav className="lp-nav">
                <div className="lp-nav-logo">
                    Slot<span>ly</span>
                </div>
                <ul className="lp-nav-links">
                    <li>
                        <a href="#features">Features</a>
                    </li>
                    <li>
                        <a href="#how">How it works</a>
                    </li>
                </ul>
                <Link to="/register" className="lp-nav-cta">
                    Get started free
                </Link>
            </nav>

            {/* HERO */}
            <section className="lp-hero">
                <div className="lp-hero-badge">
                    Now with Google Calendar sync
                </div>
                <h1 className="lp-hero-h1">
                    Scheduling that gets
                    <br />
                    <em>out of the way</em>
                </h1>
                <p className="lp-hero-sub">
                    Share a link. Let people book time with you. No
                    back-and-forth, no friction, no subscriptions to start.
                </p>
                <div className="lp-hero-actions">
                    <Link to="/register" className="lp-btn-primary">
                        Start for free
                    </Link>
                    <Link to="/login" className="lp-btn-ghost">
                        Sign in →
                    </Link>
                </div>

                <div className="lp-mock">
                    <div className="lp-mock-header">
                        <div
                            className="lp-mock-dot"
                            style={{ background: "#E24B4A" }}
                        />
                        <div
                            className="lp-mock-dot"
                            style={{ background: "#EF9F27", marginLeft: 4 }}
                        />
                        <div
                            className="lp-mock-dot"
                            style={{ background: "#639922", marginLeft: 4 }}
                        />
                        <span className="lp-mock-url">
                            app.slotly.io/book/alex-johnson
                        </span>
                    </div>
                    <div className="lp-mock-body">
                        <div className="lp-mock-col">
                            <div className="lp-mock-label">Host</div>
                            <div className="lp-mock-name">Alex Johnson</div>
                            <div className="lp-mock-meta">
                                <ClockIcon /> 30 min
                            </div>
                            <div className="lp-mock-meta">
                                <GlobeIcon /> Europe / Berlin
                            </div>
                            <p className="lp-mock-desc">
                                Product strategy call — 30 minutes to discuss
                                your roadmap.
                            </p>
                        </div>
                        <div className="lp-mock-col">
                            <div className="lp-mock-label">Pick a date</div>
                            <div className="lp-mock-grid" ref={calLabelsRef} />
                            <div className="lp-mock-grid" ref={calRef} />
                        </div>
                        <div className="lp-mock-col">
                            <div className="lp-mock-label">Available times</div>
                            <div className="lp-mock-slots" ref={slotsRef} />
                        </div>
                    </div>
                </div>
            </section>

            <div className="lp-divider" />

            {/* FEATURES */}
            <section className="lp-section" id="features">
                <div className="lp-section-label reveal">
                    Everything you need
                </div>
                <h2 className="lp-section-title reveal">
                    Built for real scheduling,
                    <br />
                    not demos
                </h2>
                <p className="lp-section-sub reveal">
                    Slotly handles the complexity so you can focus on the
                    meeting, not the calendar.
                </p>
                <div className="lp-features reveal">
                    {FEATURES.map((f) => (
                        <div key={f.title} className="lp-feature-card">
                            <div className="lp-feature-icon">{f.icon}</div>
                            <div className="lp-feature-title">{f.title}</div>
                            <div className="lp-feature-desc">{f.desc}</div>
                        </div>
                    ))}
                </div>
            </section>

            <div className="lp-divider" />

            {/* HOW IT WORKS */}
            <section className="lp-section" id="how">
                <div className="lp-section-label reveal">How it works</div>
                <h2 className="lp-section-title reveal">
                    Up and running in minutes
                </h2>
                <p className="lp-section-sub reveal">
                    No complicated setup. No training required.
                </p>
                <div className="lp-steps">
                    {STEPS.map((s, i) => (
                        <div
                            key={s.title}
                            className="lp-step reveal"
                            style={{ transitionDelay: `${i * 0.1}s` }}
                        >
                            <div className="lp-step-num">0{i + 1}</div>
                            <div className="lp-step-title">{s.title}</div>
                            <div className="lp-step-desc">{s.desc}</div>
                        </div>
                    ))}
                </div>
            </section>

            <div className="lp-divider" />

            {/* CTA */}
            <section className="lp-cta">
                <h2 className="reveal">
                    Ready to reclaim
                    <br />
                    <em>your calendar?</em>
                </h2>
                <p className="reveal">
                    Free to start. No credit card required.
                </p>
                <Link to="/register" className="lp-btn-primary reveal">
                    Create your free account
                </Link>
            </section>

            {/* FOOTER */}
            <footer className="lp-footer">
                <div className="lp-footer-logo">
                    Slot<span>ly</span>
                </div>
                <div>Built with care. © {new Date().getFullYear()} Slotly.</div>
            </footer>
        </div>
    );
};

const FEATURES = [
    {
        icon: "📅",
        title: "Smart scheduling",
        desc: "Define your working hours per day. Slotly generates available slots automatically, respecting buffers and notice periods.",
    },
    {
        icon: "🔗",
        title: "Shareable booking links",
        desc: "Every event gets a unique link. Share it anywhere — email, LinkedIn, your website. No account needed to book.",
    },
    {
        icon: "📆",
        title: "Google Calendar sync",
        desc: "Existing calendar events automatically block your availability. No double bookings, ever.",
    },
    {
        icon: "📬",
        title: "Email notifications",
        desc: "Automated confirmations and reminders for you and your attendees. Everyone knows where they need to be.",
    },
    {
        icon: "⚙️",
        title: "Booking rules",
        desc: "Set minimum notice, max advance days, buffer time between slots, and per-event capacity limits.",
    },
    {
        icon: "📋",
        title: "Custom booking forms",
        desc: "Collect what you need before the meeting — phone number, topic, notes. Custom fields per event type.",
    },
];

const STEPS = [
    {
        title: "Create your event",
        desc: "Name it, set the duration, configure your working hours. Takes two minutes.",
    },
    {
        title: "Share your link",
        desc: "Copy your unique booking URL and paste it wherever — no installation required on the other end.",
    },
    {
        title: "They pick a time",
        desc: "Your attendee sees available slots in their timezone and books in seconds.",
    },
    {
        title: "Show up and meet",
        desc: "Both of you get a confirmation. Slotly handles the reminders. You just show up.",
    },
];

const ClockIcon = () => (
    <svg width="13" height="13" viewBox="0 0 16 16" fill="none">
        <circle cx="8" cy="8" r="7" stroke="#888780" strokeWidth="1.2" />
        <path
            d="M8 4v4l2.5 2.5"
            stroke="#888780"
            strokeWidth="1.2"
            strokeLinecap="round"
        />
    </svg>
);

const GlobeIcon = () => (
    <svg width="13" height="13" viewBox="0 0 16 16" fill="none">
        <circle cx="8" cy="8" r="7" stroke="#888780" strokeWidth="1.2" />
        <path
            d="M8 1c-2 2-2 10 0 14M8 1c2 2 2 10 0 14M1 8h14"
            stroke="#888780"
            strokeWidth="1.2"
        />
    </svg>
);

export default HomePage;
