import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Home, Search, Calculator, Users, Shield, TrendingUp } from 'lucide-react';

const services = [
  {
    icon: Search,
    title: "Property Search",
    description: "Advanced search tools to find your perfect property with detailed filters and real-time updates."
  },
  {
    icon: Home,
    title: "Property Listing",
    description: "List your property with professional photography and marketing to reach qualified buyers."
  },
  {
    icon: Calculator,
    title: "Mortgage Calculator",
    description: "Calculate your monthly payments and explore financing options with our advanced tools."
  },
  {
    icon: Users,
    title: "Expert Consultation",
    description: "Get personalized advice from our experienced real estate professionals."
  },
  {
    icon: Shield,
    title: "Secure Transactions",
    description: "Safe and secure property transactions with full legal support and documentation."
  },
  {
    icon: TrendingUp,
    title: "Market Analysis",
    description: "Comprehensive market insights and property valuations to make informed decisions."
  }
];

export default function Services() {
  return (
    <section id="services" className="py-20 bg-white">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-4xl md:text-5xl font-bold text-gray-900 mb-4">
            Our Services
          </h2>
          <p className="text-xl text-gray-600 max-w-2xl mx-auto">
            Comprehensive real estate services to guide you through every step of your property journey
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
          {services.map((service, index) => (
            <Card key={index} className="group hover:shadow-lg transition-all duration-300 border-0 shadow-md hover:-translate-y-1">
              <CardHeader className="text-center pb-4">
                <div className="mx-auto w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4 group-hover:bg-blue-600 transition-colors">
                  <service.icon className="h-8 w-8 text-blue-600 group-hover:text-white transition-colors" />
                </div>
                <CardTitle className="text-xl font-semibold text-gray-900 group-hover:text-blue-600 transition-colors">
                  {service.title}
                </CardTitle>
              </CardHeader>
              <CardContent className="text-center">
                <p className="text-gray-600 leading-relaxed">
                  {service.description}
                </p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
}