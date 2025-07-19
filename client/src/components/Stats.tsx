import { Card, CardContent } from '@/components/ui/card';

const stats = [
  {
    number: "10,000+",
    label: "Properties Listed",
    description: "Active listings across all categories"
  },
  {
    number: "5,000+",
    label: "Happy Clients",
    description: "Satisfied customers and growing"
  },
  {
    number: "50+",
    label: "Expert Agents",
    description: "Professional real estate experts"
  },
  {
    number: "15+",
    label: "Years Experience",
    description: "Trusted in the real estate market"
  }
];

export default function Stats() {
  return (
    <section className="py-20 bg-blue-600">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {stats.map((stat, index) => (
            <Card key={index} className="bg-white/10 border-white/20 text-center hover:bg-white/20 transition-colors">
              <CardContent className="p-8">
                <div className="text-4xl md:text-5xl font-bold text-white mb-2">
                  {stat.number}
                </div>
                <div className="text-xl font-semibold text-blue-100 mb-2">
                  {stat.label}
                </div>
                <div className="text-blue-200 text-sm">
                  {stat.description}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </section>
  );
}