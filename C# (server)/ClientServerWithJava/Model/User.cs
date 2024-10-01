using System;

namespace Model
{
    [Serializable]
    public class User : Entity<int>
    {
        
        public string name { get; set; }

        public string password { get; set; }

        public User(int id, string name, string password) : base(id)
        {
            this.name = name;
            this.password = password;
        }

        public User(string name, string password)
        {
            this.name = name;
            this.password = password;
        }
    }
}